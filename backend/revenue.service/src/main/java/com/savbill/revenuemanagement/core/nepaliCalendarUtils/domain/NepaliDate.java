package com.savbill.revenuemanagement.core.nepaliCalendarUtils.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tblmnepalidate")
@ApiModel(value = "Customer Entity", description = "This is Nepali date entity which is used to fetch nepali data")
public class NepaliDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
	private Long id;
	
    @Column(nullable = false, length = 40)
	private String year;
	
    @Column(nullable = false, length = 40)
	private String days;
}
