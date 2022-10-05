package com.backend.birdmeal.service;

import com.backend.birdmeal.dto.*;
import com.backend.birdmeal.entity.*;
import com.backend.birdmeal.mapper.OrderDetailMapper;
import com.backend.birdmeal.mapper.OrderMapper;
import com.backend.birdmeal.mapper.ProductMapper;
import com.backend.birdmeal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerOrderService {


    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    // 구매 내역 목록 보기
    public List<SellerOrderResponseDto> getSellerOrderProduct(long sellerSeq) {
        // 리턴할 List
        List<SellerOrderResponseDto> sellerOrderResponseDtoList = new ArrayList<>();

        // 모든 orderDetail 찾기
        List<OrderDetailEntity> orderDetailEntityList = orderDetailRepository.findAllBySellerSeq(sellerSeq);

        int size = orderDetailEntityList.size();

        // 리스트 크기만큼 돌면서 반환Dto 완성하기
        for(int i=0; i<size; i++){
            // orderDetail 가져오기
            OrderDetailEntity orderDetailEntity = orderDetailEntityList.get(i);

            // order 가져오기
            OrderEntity orderEntity = orderRepository.findByOrderSeq(orderDetailEntity.getOrderSeq());

            // product 가져오기
            ProductEntity productEntity = productRepository.findByProductSeq(orderDetailEntity.getProductSeq());

            // category 가져오기
            CategoryEntity categoryEntity = categoryRepository.findByCategorySeq(productEntity.getCategorySeq());

            // user 가져오기
            Optional<UserEntity> userOptional = userRepository.findByUserSeq(orderEntity.getUserSeq());
            UserEntity userEntity = userOptional.get();

            // 가격
            int price = productEntity.getProductPrice() * orderDetailEntity.getOrderQuantity();

            // 반환값 만들기
            SellerOrderResponseDto sellerOrderResponseDto = SellerOrderResponseDto.builder()
                    .orderSeq(orderEntity.getOrderSeq())
                    .userSeq(orderEntity.getUserSeq())
                    .userNickname(userEntity.getUserNickname())
                    .userTel(userEntity.getUserTel())
                    .userAdd(userEntity.getUserAdd())
                    .userAddDetail(userEntity.getUserAddDetail())
                    .orderPrice(price)
                    .orderDetailSeq(orderDetailEntity.getOrderDetailSeq())
                    .productSeq(productEntity.getProductSeq())
                    .sellerSeq(productEntity.getSellerSeq())
                    .orderDate(orderEntity.getOrderDate())
                    .orderQuantity(orderDetailEntity.getOrderQuantity())
                    .orderTHash(orderDetailEntity.getOrderTHash())
                    .orderToState(orderDetailEntity.isOrderToState())
                    .orderDeliveryNumber(orderDetailEntity.getOrderDeliveryNumber())
                    .orderDeliveryCompany(orderDetailEntity.getOrderDeliveryCompany())
                    .categoryName(categoryEntity.getCategoryName())
                    .productName(productEntity.getProductName())
                    .productPrice(productEntity.getProductPrice())
                    .productCa(productEntity.getProductCa())
                    .productThumbnailImg(productEntity.getProductThumbnailImg())
                    .productDescriptionImg(productEntity.getProductDescriptionImg())
                    .productIsDeleted(productEntity.isProductIsDeleted())
                    .orderIsCanceled(orderDetailEntity.isOrderIsCanceled())
                    .orderIsRefunded(orderDetailEntity.isOrderIsRefunded())
                    .productCreateDate(productEntity.getProductCreateDate())
                    .productUpdateDate(productEntity.getProductUpdateDate())
                    .build();

            sellerOrderResponseDtoList.add(sellerOrderResponseDto);
        }

        return sellerOrderResponseDtoList;
    }

    // 배송 정보 입력
    public boolean updateSellerOrderInfo(OrderDeatilInfoDto orderDeatilInfoDto){
        OrderDetailEntity orderDetailEntity = orderDetailRepository.findByOrderDetailSeq(orderDeatilInfoDto.getOrderDetailSeq());

        // 주문이 없는 경우 false
        if(orderDetailEntity == null) return false;

        // 배송 정보 입력
        orderDetailEntity.setOrderDeliveryNumber(orderDeatilInfoDto.getOrderDeliveryNumber());
        orderDetailEntity.setOrderDeliveryCompany(orderDeatilInfoDto.getOrderDeliveryCompany());

        // 저장하기
        orderDetailRepository.save(orderDetailEntity);

        return true;
    }
}
