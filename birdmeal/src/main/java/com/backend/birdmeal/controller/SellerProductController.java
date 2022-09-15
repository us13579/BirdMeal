package com.backend.birdmeal.controller;

import com.backend.birdmeal.dto.ProductDto;
import com.backend.birdmeal.service.SellerProductService;
import com.backend.birdmeal.util.ResponseFrame;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("SellerProductController")
@RequiredArgsConstructor
@RequestMapping("/api/seller/product")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    /**
     * 상품 판매 등록
     *
     * @param productDto
     * @return Object
     */

    @ApiOperation(value="상품 판매 등록",response = Object.class)
    @PostMapping("")
    public ResponseEntity<?> setSellerProduct(@RequestBody ProductDto productDto){
        boolean success = sellerProductService.setSellerProduct(productDto);
        ResponseFrame<?> res;

        if(success) {
            res = ResponseFrame.of("true", "상품 판매 등록을 성공했습니다.");
        }else{
            res = ResponseFrame.of("false", "카테고리 혹은 판매자 정보가 없으므로 상품 판매 등록을 실패했습니다.");
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

//    /**
//     * 상품 정보 수정
//     *
//     * @param productDto
//     * @return Object
//     */
//
//    @ApiOperation(value="상품 정보 수정",response = Object.class)
//    @PostMapping("")
//    public ResponseEntity<?> updateSellerProduct(@RequestBody ProductDto productDto){
//        ResponseFrame<?> res = ResponseFrame.of(sellerProductService.setSellerProduct(productDto),"상품 정보 수정을 성공했습니다.");
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
}
