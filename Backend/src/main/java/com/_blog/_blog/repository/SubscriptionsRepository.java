package com._blog._blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog._blog.models.Subscriptions;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {
    List<Subscriptions> findAllBySubscriberId_Id(Long subscriberId);
    List<Subscriptions> findAllBySubscribedToId_Id(Long subscribedToId);
    List<Subscriptions> findBySubscriberId_IdAndSubscribedToId_Id(Long subscriberId, Long subscribedToId);
}
