package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.CustomerData;
import pet.store.controller.model.EmployeeData;
import pet.store.controller.model.PetStoreData;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	
	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;

	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		
		PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());
		
		/* If we are going to update an existing pet store (pet store was found),
		 * it seems that the below call shouldn't be needed. The below call will populate
		 * the contents of petStore with the contents of petStoreData. However,
		 * if a pet store was found, there is no harm in copying the fields because the values
		 * will be the same anyway. 
		 */
		copyPetStoreFields(petStore, petStoreData);
		
		PetStore dbPetStore = petStoreDao.save(petStore);
		
		return new PetStoreData(dbPetStore);
		
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
		
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {

		PetStore petStore;

		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}

		return petStore;
	}

	public PetStore findPetStoreById(Long petStoreId) {
		
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException(
					"Pet store with ID=" + petStoreId + " does not exist."));
	}

	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();		
		List<PetStoreData> response = new LinkedList<>();
		
		for(PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);
			
			psd.getCustomers().clear();
			psd.getEmployees().clear();
			
			response.add(psd);
		}		

		return response;
	}

	@Transactional(readOnly = false)
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);		
	}

	@Transactional(readOnly = false)	
	public EmployeeData saveEmployee(Long petStoreId, EmployeeData employeeData) {
		
		PetStore petStore = findPetStoreById(petStoreId);
		
		Employee employee = findOrCreateEmployee(petStoreId, employeeData.getEmployeeId());
		setEmployeeFields(employee, employeeData);
		
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		
		Employee dbEmployee = employeeDao.save(employee);
		
		return new EmployeeData(dbEmployee);
		
	}

	private void setEmployeeFields(Employee employee, EmployeeData employeeData) {
		
		employee.setEmployeeFirstName(employeeData.getEmployeeFirstName());
		employee.setEmployeeLastName(employeeData.getEmployeeLastName());
		employee.setEmployeeJobTitle(employeeData.getEmployeeJobTitle());
		employee.setEmployeePhone(employeeData.getEmployeePhone());
		
	}

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {

		Employee employee;

		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(petStoreId, employeeId);
		}

		return employee;
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException(
					"Employee with ID=" + employeeId + " does not exist."));
		
		if (employee.getPetStore().getPetStoreId() == petStoreId) {
			return employee;
		} else {
			throw new IllegalArgumentException("Employee with ID=" + employeeId +
					" does not work at the pet store with ID=" + petStoreId);					
		}
	}

	@Transactional(readOnly = false)	
	public CustomerData saveCustomer(Long petStoreId, CustomerData customerData) {
		
		PetStore petStore = findPetStoreById(petStoreId);
		
		Customer customer = findOrCreateCustomer(petStoreId, customerData.getCustomerId());
		setCustomerFields(customer, customerData);
		
		customer.getPetStores().add(petStore);
		petStore.getCustomers().add(customer);
		
		Customer dbCustomer = customerDao.save(customer);
		
		return new CustomerData(dbCustomer);
		
	}

	private void setCustomerFields(Customer customer, CustomerData customerData) {
		
		customer.setCustomerFirstName(customerData.getCustomerFirstName());
		customer.setCustomerLastName(customerData.getCustomerLastName());
		customer.setCustomerEmail(customerData.getCustomerEmail());
		
	}

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {

		Customer customer;

		if (Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(petStoreId, customerId);
		}

		return customer;
	}

	private Customer findCustomerById(Long petStoreId, Long customerId) {
		
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException(
					"Customer with ID=" + customerId + " does not exist."));
		
		boolean petStoreFound = false;
		
		for (PetStore petStore : customer.getPetStores()) {
			if (petStore.getPetStoreId().equals(petStoreId)) {
				petStoreFound = true;
			}
		}			
		
		if (petStoreFound) {
			return customer;
		} else {
			throw new IllegalArgumentException("Customer with ID=" + customerId +
					" is not a patron of the pet store with ID=" + petStoreId);					
		}
	}

}
