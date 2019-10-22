package com.wext.wextservice.controller;

import com.wext.common.domain.RepostDTO;
import com.wext.common.utils.CommonTool;
import com.wext.wextservice.domain.Repost;
import com.wext.wextservice.service.RepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repostService")
public class RepostServiceController {
    @Autowired
    private RepostService repostService;

    @GetMapping("/getRepostsFromUser/{userID}")
    public List<RepostDTO> getRepostsFromUser(@PathVariable Long userID, @RequestParam Integer page, @RequestParam Integer pageSize) {
        var reposts = repostService.getRepostsFromUser(userID, page, pageSize);
        return transToDTO(reposts);
    }

    private List<RepostDTO> transToDTO(List<Repost> reposts) {
//        List<RepostDTO> dtos = new ArrayList<>(reposts.size());
//        for (int i = 0; i < reposts.size(); i++) {
//            var dto = dtos.set(i, new RepostDTO());
//            BeanUtils.copyProperties(reposts.get(i), dto);
//        }
        return CommonTool.transBean(reposts, RepostDTO.class);
    }

}
