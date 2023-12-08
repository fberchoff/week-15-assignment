package pet.store.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import pet.store.entity.Customer;
import pet.store.entity.PetStore;

@Data
@NoArgsConstructor
public class CustomerData {
	
	private Long customerId;
	
	private String customerFirstName;
	private String customerLastName;
	private String customerEmail;	
	private Set<PetStoreResponse> petStores = new HashSet<>();
	
	public CustomerData (Customer customer) {
		
		customerId = customer.getCustomerId();
		customerFirstName = customer.getCustomerFirstName();
		customerLastName = customer.getCustomerLastName();
		customerEmail = customer.getCustomerEmail();
		
		for (PetStore petStore : customer.getPetStores()) {
			petStores.add(new PetStoreResponse(petStore));
		}
		
	}
	
	@Data
	@NoArgsConstructor
	static class PetStoreResponse {
		private Long petStoreId;
		
		private String petStoreName;
		private String petStoreAddress;
		private String petStoreCity;
		private String petStoreState;
		private String petStoreZip;
		private String petStorePhone;	
		
		PetStoreResponse(PetStore petStore) {
			petStoreId = petStore.getPetStoreId();
			petStoreName = petStore.getPetStoreName();
			petStoreAddress = petStore.getPetStoreAddress();
			petStoreCity = petStore.getPetStoreCity();
			petStoreState = petStore.getPetStoreState();
			petStoreZip = petStore.getPetStoreZip();
			petStorePhone = petStore.getPetStorePhone();
			
			}
		}
	}
