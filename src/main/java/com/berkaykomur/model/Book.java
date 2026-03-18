package com.berkaykomur.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity{

	private String title;

    @Column(nullable = false)
	private String author;
	
	@Enumerated(EnumType.STRING)
	private Category category;

    @Column(nullable = false,unique = true)
	private String isbnNo;
	
	private boolean available;
	
	
}
