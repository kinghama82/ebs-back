package com.ebs.boardparadice.model.boards;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImage {

	private String fileName;

	private int id;

	public void setId(int id) {
		this.id = id;
	}

}
