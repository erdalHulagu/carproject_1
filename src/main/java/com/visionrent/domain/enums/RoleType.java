package com.visionrent.domain.enums;

public enum RoleType {
	
	ROLE_CUSTOMER("Customer"),  //Data base'de --> ROLE_CUSTOMER ,  ROLE_ADMIN sekilinde gozukecek
	ROLE_ADMIN("Administrator"); // Customer'a --> Customer , Administrator sekilinde gozukecek
	
	private String name; 
	
	//construtoru disari acmamak icin private yaptik
	private RoleType(String name) {
		this.name=name;
	}
	public String getName() {
		return name;
		
	}

}
