package com.project.order.persistence;

/** 영속화 가능한 객체의 계약 */
public interface Persistable {
    /** 객체를 한 줄 문자열(record)로 변환 */
    String toRecord();
}