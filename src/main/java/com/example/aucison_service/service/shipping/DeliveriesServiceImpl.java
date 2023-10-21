package com.example.aucison_service.service.shipping;


import com.example.aucison_service.dto.deliveries.DeliveriesResponseDto;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.shipping.Deliveries;
import com.example.aucison_service.jpa.shipping.DeliveriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveriesServiceImpl implements DeliveriesService {
    private final DeliveriesRepository deliveriesRepository;

    @Autowired
    public DeliveriesServiceImpl(DeliveriesRepository deliveriesRepository) {
        this.deliveriesRepository = deliveriesRepository;
    }

//    @Override
//    public Long saveDelivery(DeliveriesCreateDto deliveriesCreateDto) { //배송지 저장
//        Deliveries delivery = Deliveries.builder()
//                .addrName(deliveriesCreateDto.getAddrName())
//                .zipNum(deliveriesCreateDto.getZipNum())
//                .addr(deliveriesCreateDto.getAddr())
//                .addrDetail(deliveriesCreateDto.getAddrDetail())
//                .name(deliveriesCreateDto.getName())
//                .tel(deliveriesCreateDto.getTel())
//                .isCompleted(deliveriesCreateDto.isCompleted())
//                .isStarted(deliveriesCreateDto.isStarted())
//                .build();
//
//        delivery = deliveriesRepository.save(delivery);
//        return delivery.getDeliveriesId();
//    }

    @Override
    @Transactional(readOnly = true)
    public DeliveriesResponseDto getDeliveryByOrdersId(Long ordersId) {
        //orders_id로 배송지 정보 조회
        Deliveries delivery = deliveriesRepository.findByOrdersOrdersId(ordersId);

        if (delivery == null) {
            throw new AppException(ErrorCode.DELIVERY_NOT_FOUND);
        }

        return DeliveriesResponseDto.builder()
                .addrName(delivery.getAddrName())
                .name(delivery.getName())
                .addr(delivery.getAddr())
                .addrDetail(delivery.getAddrDetail())
                .tel(delivery.getTel())
                .isCompleted(delivery.isCompleted())
                .isStarted(delivery.isStarted())
                .build();
    }
}