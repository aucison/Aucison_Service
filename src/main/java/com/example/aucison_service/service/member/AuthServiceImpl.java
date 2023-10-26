package com.example.aucison_service.service.member;


import com.example.aucison_service.dto.auth.MemberDto;
import com.example.aucison_service.dto.auth.MembersInfoDto;
import com.example.aucison_service.jpa.member.*;
import com.example.aucison_service.util.JwtUtils;
import com.example.aucison_service.vo.RequestLoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final MembersRepository membersRepository;
    private final MembersInfoRepository membersInfoRepository;
    private final MembersImgRepository membersImgRepository;
    private final JwtUtils jwtUtils;

    @Override
    public MemberDto createMember(MemberDto memberDto) {

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

        if(membersEntity != null) {
            // MembersEntity -> MemberDto
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            MemberDto memberDto = mapper.map(membersEntity, MemberDto.class);

            String accessToken = jwtUtils.createAccessToken(memberDto);
            String refreshToken = jwtUtils.getRefreshToken(memberDto.getEmail());

            jwtUtils.updateRefreshToken(memberDto, refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.add("accessToken", accessToken);
            headers.add("refreshToken", refreshToken);

            return (ResponseEntity) ResponseEntity.ok().headers(headers);

        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void logout(String accessToken) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        jwtUtils.deleteRefreshToken(email);
        jwtUtils.setBlackList(accessToken);
    }


    @Override
    public MembersInfoDto getMember(String accessToken) {
        String email = jwtUtils.getEmailFromToken(accessToken);
        if(email != null) {
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
