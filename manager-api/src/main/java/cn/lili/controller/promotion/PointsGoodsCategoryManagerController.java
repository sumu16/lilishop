package cn.lili.controller.promotion;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.utils.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.promotion.entity.dos.PointsGoodsCategory;
import cn.lili.modules.promotion.entity.vos.PointsGoodsCategoryVO;
import cn.lili.modules.promotion.service.PointsGoodsCategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端,积分商品分类接口
 *
 * @author paulG
 * @date 2021/1/14
 **/
@RestController
@Api(tags = "管理端,积分商品分类接口")
@RequestMapping("/manager/promotion/pointsGoodsCategory")
public class PointsGoodsCategoryManagerController {
    @Autowired
    private PointsGoodsCategoryService pointsGoodsCategoryService;

    @PostMapping
    @ApiOperation(value = "添加积分商品分类")
    public ResultMessage<Object> add(PointsGoodsCategoryVO pointsGoodsCategory) {
        if (pointsGoodsCategoryService.addCategory(pointsGoodsCategory)) {
            return ResultUtil.success(ResultCode.SUCCESS);
        }
        return ResultUtil.error(ResultCode.ERROR);
    }

    @PutMapping
    @ApiOperation(value = "修改积分商品分类")
    public ResultMessage<Object> update(PointsGoodsCategoryVO pointsGoodsCategory) {
        if (pointsGoodsCategoryService.updateCategory(pointsGoodsCategory)) {
            return ResultUtil.success(ResultCode.SUCCESS);
        }
        return ResultUtil.error(ResultCode.ERROR);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除积分商品分类")
    public ResultMessage<Object> delete(@PathVariable String id) {
        if (pointsGoodsCategoryService.deleteCategory(id)) {
            return ResultUtil.success(ResultCode.SUCCESS);
        }
        return ResultUtil.error(ResultCode.ERROR);
    }

    @GetMapping
    @ApiOperation(value = "获取积分商品分类分页")
    public ResultMessage<IPage<PointsGoodsCategory>> page(String name, PageVO page) {
        return ResultUtil.data(pointsGoodsCategoryService.getCategoryByPage(name, page));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "修改积分商品分类")
    public ResultMessage<Object> getById(@PathVariable String id) {
        return ResultUtil.data(pointsGoodsCategoryService.getCategoryDetail(id));
    }

}
