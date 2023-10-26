package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.util.JwtUtils;
import com.example.aucison_service.vo.RequestLoginVo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersImgRepository membersImgRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        // 이메일 중복 검사
        if (membersRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Error: 중복된 이메일");
        }
        MembersEntity membersEntity = MembersEntity.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
//                        .nickname(memberDto.getNickname())
                .build();

        membersRepository.save(membersEntity);

        return new ModelMapper().map(membersEntity, MemberDto.class);
    }

    @Override
    public ResponseEntity login(RequestLoginVo requestLoginVo) {
        MembersEntity membersEntity = membersRepository.findByEmail(requestLoginVo.getEmail());

        if (membersEntity == null) {
            // 이메일로 사용자를 찾을 수 없는 경우, 명확한 예외 메시지와 함께 예외를 발생
            throw new UsernameNotFoundException("User not found with email: " + requestLoginVo.getEmail());
        }

        // DTO 변환(MembersEntity -> MemberDto)
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberDto memberDto = mapper.map(membersEntity, MemberDto.class);

        // JWT 토큰 생성
        String accessToken = jwtUtils.createAccessToken(memberDto);
        String refreshToken = jwtUtils.createRefreshToken(memberDto);

        // 응답 헤더에 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("RefreshToken", refreshToken);

        // 응답 본문에도 토큰 정보를 넣을 수 있음 (선택사항)
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok()
                .headers(headers) // 헤더에 토큰을 포함시킬 수 있음
                .body(tokens); // 응답 본문에 토큰을 넣음, 이 부분은 선택사항
    }

    //현재 미구현으로 두는게 나음, 그 이유는 블랙리스트관련 서비스를 아직 만들지 않음 -> 결국에는 만들어야하나 원할한 테스트를 위해 주석 하는게 나아보임
    @Override
    public void logout(String accessToken) {
        // 토큰에서 이메일 추출
        String email = jwtUtils.getEmailFromToken(accessToken);

        if(email == null) {
            throw new RuntimeException("Could not find email in the token");
        }

        // refreshToken 무효화 (DB에서 삭제 등)
        jwtUtils.deleteRefreshToken(email);

        // accessToken을 블랙리스트에 추가하여 더 이상 사용할 수 없도록 함
        jwtUtils.setBlackList(accessToken);
    }



    //이 아래로 아직 미수정!!!!
    //이 아래로 아직 미수정!!!!
    @Override
    public MembersInfoDto getMember(String accessToken) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        if (email != null) {
            // null 검증 로직 추가하기
            MembersEntity membersEntity = membersRepository.findByEmail(email);
            MembersInfo membersInfo = membersInfoRepository.findByMembers(membersEntity);
            MembersImg membersImgEntity = membersImgRepository.findByMembersInfo(membersInfo);

            return MembersInfoDto.builder()
                    .subEmail(membersInfo.getSubEmail())
                    .name(membersEntity.getName())
                    .nickName(membersEntity.getNickname())
                    .phone(membersInfo.getPhone())
                    .imgUrl(membersImgEntity.getUrl())
                    .build();
        } else {
            throw new RuntimeException();
        }
    }

    @Transactional
    @Override
    public void patchMember(String accessToken, MembersInfoDto membersInfoDto) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        MembersEntity membersEntity = membersRepository.findByEmail(email);
        MembersInfo membersInfo = membersInfoRepository.findByMembers(membersEntity);
        MembersImg membersImg = membersImgRepository.findByMembersInfo(membersInfo);

        membersImg.updateInfo(
                membersInfo.updateInfo(
                        membersEntity.updateInfo(membersInfoDto),
                        membersInfoDto),
                membersInfoDto);
    }

    @Override
    public Iterable<MembersEntity> getMemberByAll() {
        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
