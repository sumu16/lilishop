package cn.lili.modules.order.cart.render.impl;

import cn.hutool.core.util.NumberUtil;
import cn.lili.common.utils.CurrencyUtil;
import cn.lili.modules.order.cart.entity.dto.TradeDTO;
import cn.lili.modules.order.cart.entity.vo.CartSkuVO;
import cn.lili.modules.order.cart.render.CartRenderStep;
import cn.lili.modules.store.entity.dos.FreightTemplateChild;
import cn.lili.modules.store.entity.dto.FreightTemplateChildDTO;
import cn.lili.modules.store.entity.enums.FreightTemplateEnum;
import cn.lili.modules.store.entity.vos.FreightTemplateVO;
import cn.lili.modules.store.service.FreightTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * sku 运费计算
 *
 * @author Chopper
 * @date 2020-07-02 14:47
 */
@Order(4)
@Service
public class SkuFreightRender implements CartRenderStep {

    @Autowired
    private FreightTemplateService freightTemplateService;

    @Override
    public void render(TradeDTO tradeDTO) {
        List<CartSkuVO> cartSkuVOS = tradeDTO.getSkuList();
        for (CartSkuVO cartSkuVO : cartSkuVOS) {
            String freightTemplateId = cartSkuVO.getGoodsSku().getFreightTemplateId();
            //免运费则跳出运费计算
            if (Boolean.TRUE.equals(cartSkuVO.getIsFreeFreight()) || freightTemplateId == null) {
                continue;
            }
            //寻找对应对商品运费计算模版
            FreightTemplateVO freightTemplate = freightTemplateService.getFreightTemplate(freightTemplateId);
            if (freightTemplate != null && freightTemplate.getFreightTemplateChildList() != null && !freightTemplate.getFreightTemplateChildList().isEmpty()) {
                FreightTemplateChild freightTemplateChild = freightTemplate.getFreightTemplateChildList().get(0);
                FreightTemplateChildDTO freightTemplateChildDTO = new FreightTemplateChildDTO(freightTemplateChild);
                freightTemplateChildDTO.setPricingMethod(freightTemplate.getPricingMethod());

                //要计算的基数 数量/重量
                Double count = (freightTemplateChildDTO.getPricingMethod().equals(FreightTemplateEnum.NUM.name())) ?
                        cartSkuVO.getNum() :
                        cartSkuVO.getGoodsSku().getWeight() * cartSkuVO.getNum();

                //计算运费
                Double countFreight = countFreight(count, freightTemplateChildDTO);
                //写入运费
                cartSkuVO.getPriceDetailDTO().setFreightPrice(countFreight);
                //运费逻辑处理
                if (tradeDTO.getPriceDetailDTO().getFreightPrice() != null) {
                    tradeDTO.getPriceDetailDTO().setFreightPrice(CurrencyUtil.add(tradeDTO.getPriceDetailDTO().getFreightPrice(), countFreight));
                } else {
                    tradeDTO.getPriceDetailDTO().setFreightPrice(countFreight);
                }
            }
        }
    }

    /**
     * 计算运费
     *
     * @param count    重量/件
     * @param template 计算模版
     * @return 运费
     */
    private Double countFreight(Double count, FreightTemplateChildDTO template) {
        try {
            Double finalFreight = template.getFirstPrice();
            //不满首重
            if (template.getFirstCompany() >= count) {
                return finalFreight;
            }
            Double continuedCount = count - template.getFirstCompany();
            // 计算续重价格
            return CurrencyUtil.add(finalFreight,
                    CurrencyUtil.mul(NumberUtil.parseInt(String.valueOf((continuedCount / template.getContinuedCompany()))), template.getContinuedPrice()));
        } catch (Exception e) {
            return 0D;
        }


    }


}
