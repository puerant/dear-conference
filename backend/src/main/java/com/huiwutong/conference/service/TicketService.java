package com.huiwutong.conference.service;

import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.service.dto.ticket.TicketStatisticsVo;
import com.huiwutong.conference.service.dto.ticket.TicketVo;

import java.util.List;

/**
 * 票务服务接口
 */
public interface TicketService {

    List<Ticket> listActive();

    /**
     * 分页查询票务列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param status   状态筛选（可选）：0-下架，1-上架
     * @return 分页结果
     */
    PageResult<TicketVo> list(Integer page, Integer pageSize, Integer status);

    /**
     * 查询可购买票务（库存>0且状态=上架）
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<TicketVo> listAvailable(Integer page, Integer pageSize);

    Ticket getById(Long id);

    Ticket create(Ticket ticket);

    void update(Ticket ticket);

    void updateStatus(Long id, Integer status);

    /**
     * 调整库存
     *
     * @param id    票务ID
     * @param stock 新库存数量
     * @throws com.huiwutong.conference.common.exception.BusinessException 调整后库存低于已售数量时抛出
     */
    void updateStock(Long id, Integer stock);

    /**
     * 删除票务
     *
     * @param id 票务ID
     * @throws com.huiwutong.conference.common.exception.BusinessException 已售出时抛出
     */
    void delete(Long id);

    /**
     * 获取票务统计数据
     *
     * @return 统计数据
     */
    TicketStatisticsVo getStatistics();
}
