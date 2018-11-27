package cn.spicybar.safepass.controller;

import cn.spicybar.safepass.domain.Info;
import cn.spicybar.safepass.security.JwtUser;
import cn.spicybar.safepass.service.IInfoService;
import cn.spicybar.safepass.utils.ParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 信息管理Controller
 *
 * @author hackyo
 * Created on 2017/12/28 22:41.
 */
@CrossOrigin(origins = {"https://api.spicybar.cn", "https://safepass.spicybar.cn"}, allowCredentials = "true")
@RestController
@RequestMapping(value = "/info", produces = "text/html;charset=UTF-8")
public class InfoController {

    private IInfoService infoService;
    private ObjectMapper mapper;

    @Autowired
    public InfoController(IInfoService infoService) {
        this.infoService = infoService;
        this.mapper = new ObjectMapper();
    }

    /**
     * 新增信息
     *
     * @param info          信息
     * @param bindingResult 验证信息
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/insert")
    public String insert(@Validated Info info, BindingResult bindingResult) throws JsonProcessingException {
        String returnValue;
        if (bindingResult.hasErrors()) {
            throw new ParameterException(bindingResult);
        } else {
            JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            info.setUserId(user.getId());
            returnValue = infoService.insert(info);
        }
        return mapper.writeValueAsString(returnValue);
    }

    /**
     * 根据页码获取信息
     *
     * @param page 页码
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/getForPage")
    public String getForPage(int page) throws JsonProcessingException {
        JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> renturMap = new HashMap<>(2);
        renturMap.put("info", infoService.getInfoForPage(user.getId(), page));
        renturMap.put("count", infoService.getCount(user.getId()));
        return mapper.writeValueAsString(renturMap);
    }

    /**
     * 删除指定信息
     *
     * @param id 信息ID
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/delete")
    public String delete(String id) throws JsonProcessingException {
        JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mapper.writeValueAsString(infoService.delete(user.getId(), id));
    }

    /**
     * 更新信息
     *
     * @param info          信息
     * @param bindingResult 验证信息
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/update")
    public String update(@Validated Info info, BindingResult bindingResult) throws JsonProcessingException {
        String returnValue;
        if (bindingResult.hasErrors()) {
            throw new ParameterException(bindingResult);
        } else {
            JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            info.setUserId(user.getId());
            returnValue = infoService.update(info);
        }
        return mapper.writeValueAsString(returnValue);
    }

    /**
     * 验证信息描述是否存在
     *
     * @param id          信息ID
     * @param description 信息描述
     * @return 验证结果
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/descriptionExists")
    public String descriptionExists(String id, String description) throws JsonProcessingException {
        JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mapper.writeValueAsString(infoService.descriptionExists(id, user.getId(), description));
    }

    /**
     * 描述的模糊查询
     *
     * @param search 搜索内容
     * @return 搜索结果
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/search")
    public String search(String search) throws JsonProcessingException {
        JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mapper.writeValueAsString(infoService.searchInfo(user.getId(), search));
    }

}
