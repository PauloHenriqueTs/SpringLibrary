
package com.example.library.image;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotEmpty;

@Document
public class Image {

	@Id
	final private String id;

	@NotEmpty(message = "This field is required")
	final private String name;

	public Image(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
