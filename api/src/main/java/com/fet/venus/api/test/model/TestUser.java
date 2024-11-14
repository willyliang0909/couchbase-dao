package com.fet.venus.api.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestUser implements Serializable {

    private String name;
    private Long age;
    private Address address;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address implements Serializable {
        private String country;
        private String city;
        private String street;
    }
}

