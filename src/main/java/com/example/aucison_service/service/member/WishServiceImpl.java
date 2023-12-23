package com.example.aucison_service.service.member;



import com.example.aucison_service.jpa.member.MembersRepository;
import com.example.aucison_service.jpa.member.WishesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {
    ;
    private final MembersRepository membersRepository;
    private final WishesRepository wishesRepository;



}
