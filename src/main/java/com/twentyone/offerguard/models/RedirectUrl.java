package com.twentyone.offerguard.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "redirect_urls")
@RequiredArgsConstructor
@NoArgsConstructor
public class RedirectUrl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NonNull
	private String offerId;

	@NonNull
	private String url;

}
