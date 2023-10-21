package com.example.aucison_service.service.shipping;

import com.example.aucison_service.dto.orders.OrdersResponseDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.shipping.Orders;
import com.example.aucison_service.jpa.shipping.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl implements OrdersService{
    private final OrdersRepository ordersRepository;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrdersServiceImpl(OrdersRepository ordersRepository, ProductServiceClient productServiceClient) {
        this.ordersRepository = ordersRepository;
        this.productServiceClient = productServiceClient;
    }

//    @Transactional
//    @Override
//    public Long saveOrder(OrdersCreateDto ordersCreateDto) {
//        // DTO로부터 새로운 Orders 엔터티를 생성합니다.
//        Orders orders = Orders.builder()
//                .productsId(ordersCreateDto.getProductsId())
//                .email(ordersCreateDto.getEmail())
////                .createdTime(LocalDateTime.now()) // BaseTimeEntity를 기반으로 자동 생성될 수 있습니다.
//                .status(ordersCreateDto.getStatus())
//                .build();
//
//        // 데이터베이스에 저장합니다.
//        Orders savedOrder = ordersRepository.save(orders);
//
//        // 저장된 주문의 ID를 반환합니다.
//        return savedOrder.getOrdersId();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdersResponseDto> findAllOrdersByEmail(String email) { //email로 주문내역 전체 조회
        List<Orders> orders = ordersRepository.findAllByEmail(email);

        //TODO: 이메일로 내역 조회를 그대로 유지할 것인지에 따라 email 유효 검증 추가 유무가 달라짐

        if (orders.isEmpty()) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 각 주문을 DTO로 변환하여 리스트로 반환한다.
        return orders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersResponseDto findOrderByOrdersId(Long ordersId) { // ordersId로 주문내역 개별 조회
        // 주어진 ID를 가진 주문 정보를 찾는다. 찾지 못할 경우 RuntimeException 발생
        Orders order = ordersRepository.findById(ordersId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // 주문 정보를 DTO로 변환하여 반환한다.
        return convertToDto(order);
    }

//    @Transactional
//    @Override
//    public void deleteOrder(Long ordersId) {
//        // 해당 ID의 주문이 있는지 확인
//        /**
//         * Optional은 Java 8에서 도입된 클래스로, null을 직접 다루는 대신 감싸서 다룰 수 있게 해줍니다.
//         * 이를 통해 명시적으로 해당 값이 존재할 수도, 존재하지 않을 수도 있다는 것을 코드 상에서 바로 알 수 있게 됩니다.
//         * 또한, Optional은 여러가지 유용한 메서드를 제공하여 null 체크 로직을 더 간결하고 가독성 좋게 작성할 수 있게 도와줍니다.
//         */
//        Optional<Orders> optionalOrder = ordersRepository.findById(ordersId);
//        if (optionalOrder.isEmpty()) {
//            throw new NoSuchElementException("No order found with ID " + ordersId);
//        }
//
//        // 주문 삭제
//        ordersRepository.deleteById(ordersId);
//    }

    private OrdersResponseDto convertToDto(Orders order) { // 주문 정보(Orders)를 응답용 DTO로 변환하는 메서드
        //products_id로 product 정보를 가져옴
        ProductInfoResponseDto productInfoResponse = productServiceClient.getOrderProductByProductsId(order.getProductsId());

        //주문 일자 형식
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return OrdersResponseDto.builder()
                .productName(productInfoResponse.getProductName())
                .productImg(productInfoResponse.getProductImg())
                .productDescription(productInfoResponse.getProductDescription())
                .category(productInfoResponse.getCategory())
                .ordersId(order.getOrdersId())
                .price(productInfoResponse.getPrice())
                .createdTime(order.getCreatedDate().toLocalDate().format(formatter))
                .status(order.getStatus())
                .build();
    }
}
