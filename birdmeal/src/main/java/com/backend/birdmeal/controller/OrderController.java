package com.backend.birdmeal.controller;

import com.backend.birdmeal.dto.MyOrderResponseDto;
import com.backend.birdmeal.dto.OrderRequestDto;
import com.backend.birdmeal.entity.UserEntity;
import com.backend.birdmeal.repository.UserRepository;
import com.backend.birdmeal.service.OrderService;
import com.backend.birdmeal.util.ResponseFrame;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api("OrderController")
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    // 나중에 Service로 대체 ( 회원정보 불러오기로 )
    private final UserRepository userRepository; 

    /**
     * 주문 내역 저장
     *
     * @param orderRequestList
     * @return Object
     */

    @ApiOperation(value="주문 내역 저장",response = Object.class)
    @PostMapping("")
    public ResponseEntity<?> setOrderInfo(@RequestBody List<OrderRequestDto> orderRequestList){
        boolean success = orderService.setOrderInfo(orderRequestList);
        ResponseFrame<?> res;


        if(success) {
            res = ResponseFrame.of(success, "주문 내역 저장을 성공했습니다.");
        }else{
            res = ResponseFrame.of(success, "상품을 판매하지 않아 주문 내역 저장을 실패했습니다.");
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 내 주문 불러오기
     *
     * @param userSeq
     * @return Object
     */

    @ApiOperation(value="내 주문 불러오기",response = Object.class)
    @GetMapping("/list/{user-seq}")
    public ResponseEntity<?> getMyOrderInfo(@PathVariable("user-seq") long userSeq){
        List<MyOrderResponseDto> list = orderService.getMyOrderInfo(userSeq);
        ResponseFrame<?> res;

        // 회원정보 확인
        UserEntity userEntity = userRepository.findByUserSeq(userSeq);
        
        if(userEntity == null) {
            res = ResponseFrame.of(false, "사용자가 없어 내 주문 불러오기을 실패했습니다.");
        }else{
            res = ResponseFrame.of(list, "내 주문 불러오기을 성공했습니다.");
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
