package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.entity.OrderTaskConfig;
import com.huiwutong.conference.mapper.OrderTaskConfigMapper;
import com.huiwutong.conference.service.OrderTaskConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 订单定时任务配置服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderTaskConfigServiceImpl implements OrderTaskConfigService {

    private final OrderTaskConfigMapper configMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "system:order_task_config:v2";

    @Override
    public OrderTaskConfig getConfig() {
        OrderTaskConfig config = null;

        try {
            Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached instanceof OrderTaskConfig) {
                config = (OrderTaskConfig) cached;
            } else if (cached != null) {
                // 缓存格式不兼容（如 LinkedHashMap），清除旧缓存
                redisTemplate.delete(CACHE_KEY);
            }
        } catch (Exception e) {
            // 序列化异常或其他异常，清除旧缓存
            redisTemplate.delete(CACHE_KEY);
        }

        if (config == null) {
            config = configMapper.selectOne(
                new LambdaQueryWrapper<OrderTaskConfig>()
                    .last("LIMIT 1")
            );
            if (config == null) {
                // 默认配置
                config = new OrderTaskConfig();
                config.setTimeoutMinutes(30);
                config.setCheckIntervalMinutes(5);
                config.setCancelTaskEnabled(1);
                config.setRefundTaskEnabled(1);
            }
            redisTemplate.opsForValue().set(CACHE_KEY, config, 1, TimeUnit.HOURS);
        }
        return config;
    }

    @Override
    @Transactional
    public void saveOrUpdate(OrderTaskConfig config) {
        OrderTaskConfig existing = configMapper.selectOne(
            new LambdaQueryWrapper<OrderTaskConfig>()
                .last("LIMIT 1")
        );

        if (existing == null) {
            configMapper.insert(config);
        } else {
            config.setId(existing.getId());
            configMapper.updateById(config);
        }
        // 清除缓存
        redisTemplate.delete(CACHE_KEY);
    }
}
