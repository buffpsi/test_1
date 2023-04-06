package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = likeablePersonService.findByFromInstaMemberId(instaMember.getId());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }

    //3번코드
    @GetMapping("delete/{id}")
    public String deleteLikeablePerson(@PathVariable Integer id) {
        Optional<LikeablePerson> optionalLikeablePerson = likeablePersonService.findById(id);
        LikeablePerson likeablePerson = optionalLikeablePerson.get();
        //컨트롤러에서 가장 기초적인 권한체크는 해준다.
        if (rq.getMember().getInstaMember().getId() != likeablePerson.getFromInstaMember().getId()) {
            return rq.historyBack("삭제 권한이 없습니다!");
        }
        //권한이 있다면 서비스에서 삭제 메서드를 요청한다.
        RsData<LikeablePerson> likeablePersonRsData = likeablePersonService.delete(id);
        return rq.redirectWithMsg("/likeablePerson/list", likeablePersonRsData);
    }

//    //2번코드 Controller
//    @GetMapping("delete/{id}")
//    public String deleteLikeablePerson(@PathVariable Integer id) {
//        RsData<LikeablePerson> likeablePersonRsData = likeablePersonService.delete(rq.getMember(), id);
//        return rq.redirectWithMsg("/likeablePerson/list", likeablePersonRsData);
//    }


//    //===================================================================================
//    //1번 코드
//    //목표: 호감상대이 삭제되면 rq.redirectWithMsg를 통해 안내메세지로 알려주고 삭제된 페이지를 보여줄 것
//    @GetMapping("delete/{id}")
//    public String deleteLikeablePerson(@PathVariable Integer id) {
//        RsData<LikeablePerson> likeablePersonRsData = likeablePersonService.delete(id);
//        if (likeablePersonRsData.isSuccess()) {
//            return rq.redirectWithMsg("/likeablePerson/list", likeablePersonRsData);
//        } else {
//            return rq.historyBack(likeablePersonRsData);
//        }
//    }
}
