package com.namora.user.services;

import com.namora.user.dto.AddressRequest;
import com.namora.user.dto.ApiResponse;
import com.namora.user.entities.Address;
import com.namora.user.entities.Customer;
import com.namora.user.entities.User;
import com.namora.user.repositories.AddressRepository;
import com.namora.user.repositories.CustomerRepository;
import com.namora.user.repositories.UserRepository;
import com.namora.user.storage.UserContext;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> addAddress(AddressRequest addressRequest, String customerId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("CUSTOMER"))
            return new ResponseEntity<>(ApiResponse.error("Only Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Customer not found!"), HttpStatus.NOT_FOUND);
        Address address = new Address();
        address.setCustomer(optionalCustomer.get());
        address.setAddress(addressRequest.address());
        address.setLatitude(addressRequest.latitude());
        address.setLongitude(addressRequest.longitude());
        Point location = geometryFactory.createPoint(new Coordinate(addressRequest.longitude(), addressRequest.latitude()));
        address.setLocation(location);
        Address savedAddress = addressRepository.save(address);
        return new ResponseEntity<>(ApiResponse.success("Address created successfully!", savedAddress), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateAddress(AddressRequest addressRequest, String customerId, String addressId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("CUSTOMER"))
            return new ResponseEntity<>(ApiResponse.error("Only Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Customer not found!"), HttpStatus.NOT_FOUND);
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        if (optionalAddress.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Address not found!"), HttpStatus.NOT_FOUND);
        Address address = optionalAddress.get();
        address.setLatitude(address.getLatitude());
        address.setLongitude(address.getLongitude());
        address.setAddress(address.getAddress());
        Point location = geometryFactory.createPoint(new Coordinate(addressRequest.longitude(), addressRequest.latitude()));
        address.setLocation(location);
        Address savedAddress = addressRepository.save(address);
        return new ResponseEntity<>(ApiResponse.success("Address updated successfully!", savedAddress), HttpStatus.CREATED);
    }

    public ResponseEntity<?> makeDefaultAddress(String customerId, String addressId) {
        String userRole = UserContext.getCurrentUserRole();
        String userId = UserContext.getCurrentUserId();
        if (!userRole.equals("CUSTOMER"))
            return new ResponseEntity<>(ApiResponse.error("Only Customers are allowed!"), HttpStatus.FORBIDDEN);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("User not found!"), HttpStatus.NOT_FOUND);
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Customer not found!"), HttpStatus.NOT_FOUND);
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        if (optionalAddress.isEmpty())
            return new ResponseEntity<>(ApiResponse.error("Address not found!"), HttpStatus.NOT_FOUND);
        List<Address> customerAddresses = addressRepository.findByCustomer(optionalCustomer.get());

        System.out.println(customerAddresses);
        Address previousDefaultAddress = null;
        for (Address temp : customerAddresses) {
            String address = temp.getAddress();
            if (address != null && address.startsWith("DEFAULT_")) {
                previousDefaultAddress = temp;
                break;
            }
        }
        if (previousDefaultAddress != null && !previousDefaultAddress.getId().equals(addressId)) {
            String address = previousDefaultAddress.getAddress();
            String newAddress = address.replace("DEFAULT_", "");
            previousDefaultAddress.setAddress(newAddress);
            addressRepository.save(previousDefaultAddress);
            Address newDefaultAddress = optionalAddress.get();
            String newDefaultAddressToBeAdded = STR."DEFAULT_\{newDefaultAddress.getAddress()}";
            newDefaultAddress.setAddress(newDefaultAddressToBeAdded);
            addressRepository.save(newDefaultAddress);
        } else if (previousDefaultAddress == null) {
            Address newDefaultAddress = optionalAddress.get();
            String newDefaultAddressToBeAdded = STR."DEFAULT_\{newDefaultAddress.getAddress()}";
            newDefaultAddress.setAddress(newDefaultAddressToBeAdded);
            addressRepository.save(newDefaultAddress);
        }
        List<Address> addresses = addressRepository.findByCustomer(optionalCustomer.get());
        return new ResponseEntity<>(ApiResponse.success("Address updated successfully!", addresses), HttpStatus.OK);
    }

}
