package com.test;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysChannel  implements Serializable {

	private String id;

	private String name;

	private java.util.Date createdTime;

	private java.util.Date updatedTime;

	private String createdBy;

	private String updatedBy;

}
