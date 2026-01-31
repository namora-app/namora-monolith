# Namora Food Delivery - API Documentation

## Overview
Namora is a microservices-based food delivery platform built with Spring Boot. The system consists of multiple services including Identity, User, Restaurant, Order, Payment, Dispatch, Gateway, and Eureka Server.

## Architecture
- **Gateway Service**: API Gateway (Port 8090)
- **Eureka Server**: Service Discovery (Port 8761)
- **Identity Service**: Authentication & Authorization (Port 8082)
- **User Service**: User, Customer, Rider, Cart Management (Port 8086)
- **Restaurant Service**: Restaurant & Menu Management (Port 8085)
- **Order Service**: Order Processing (Port 8083)
- **Payment Service**: Payment Processing (Port 8084)
- **Dispatch Service**: Rider Dispatch & Location Tracking (Port 8087)

## Base URLs
- Gateway: `http://localhost:8090`
- Direct Service Access: `http://localhost:{service-port}`

## Authentication
All authenticated endpoints require a JWT token passed via:
1. Cookie: `accessToken`
2. Header: `Authorization: Bearer {token}`

The gateway automatically extracts user information and forwards it to downstream services via headers:
- `X-User-ID`: User's unique identifier
- `X-Role`: User's role (CUSTOMER, RIDER, RESTAURANT_OWNER, ADMIN)

## User Roles
- **CUSTOMER**: Can order food, manage cart, addresses
- **RIDER**: Delivery personnel, can update location
- **RESTAURANT_OWNER**: Can manage restaurants, menus, schedules
- **ADMIN**: System administration

---

## API Endpoints

### 1. Identity Service (`/auth/**`)

#### 1.1 Register User
**POST** `/auth/register`
- **Public**: Yes
- **Description**: Create a new user account
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```
- **Response**: Sets cookies (accessToken, refreshToken)

#### 1.2 Login
**POST** `/auth/login`
- **Public**: Yes
- **Description**: Authenticate user
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
- **Response**: Sets cookies (accessToken, refreshToken)

#### 1.3 Refresh Token
**POST** `/auth/refresh`
- **Public**: Yes
- **Description**: Refresh access token using refresh token
- **Headers**: Requires refreshToken cookie
- **Response**: New accessToken and refreshToken

#### 1.4 Logout
**POST** `/auth/logout`
- **Public**: Yes
- **Description**: Logout user and invalidate tokens
- **Response**: Clears authentication cookies

---

### 2. User Service

#### 2.1 User Management

##### 2.1.1 Create User Profile
**POST** `/users`
- **Auth Required**: Yes (CUSTOMER or RIDER)
- **Description**: Create user profile after registration
- **Request Body**:
```json
{
  "name": "John Doe",
  "phoneNumber": "+1234567890"
}
```

##### 2.1.2 Update User Profile
**PUT** `/users`
- **Auth Required**: Yes (CUSTOMER or RIDER)
- **Description**: Update user profile
- **Request Body**:
```json
{
  "name": "John Updated",
  "phoneNumber": "+1234567890"
}
```

##### 2.1.3 Get User Info
**GET** `/users`
- **Auth Required**: Yes (CUSTOMER or RIDER)
- **Description**: Get current user information

#### 2.2 Customer Addresses

##### 2.2.1 Add Address
**POST** `/customers/{customerId}/addresses`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Add a new delivery address
- **Request Body**:
```json
{
  "latitude": 12.9716,
  "longitude": 77.5946,
  "address": "123 Main Street, Bangalore, Karnataka"
}
```

##### 2.2.2 Update Address
**PUT** `/customers/{customerId}/addresses/{addressId}`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Update an existing address
- **Request Body**:
```json
{
  "latitude": 12.9716,
  "longitude": 77.5946,
  "address": "456 New Street, Bangalore, Karnataka"
}
```

##### 2.2.3 Make Default Address
**PUT** `/customers/{customerId}/addresses/{addressId}/default`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Set an address as default

#### 2.3 Rider Management

##### 2.3.1 Submit Rider Details
**POST** `/riders/{riderId}/submit`
- **Auth Required**: Yes (RIDER)
- **Description**: Submit rider credentials for approval
- **Request Body**:
```json
{
  "licenseNumber": "DL1234567890",
  "vehicleNumber": "KA-01-AB-1234"
}
```

#### 2.4 Cart Management

##### 2.4.1 Add Item to Cart
**POST** `/carts/add`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Add or update item in cart
- **Request Body**:
```json
{
  "itemId": "item-uuid",
  "quantity": 2
}
```

##### 2.4.2 Clear Cart
**POST** `/carts/clear`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Remove all items from cart

---

### 3. Restaurant Service

#### 3.1 Restaurant Management

##### 3.1.1 Create Restaurant
**POST** `/restaurants`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Register a new restaurant
- **Request Body**:
```json
{
  "name": "Pizza Palace",
  "address": "789 Food Street, Bangalore",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "fssaiLicense": "12345678901234"
}
```

##### 3.1.2 Update Restaurant
**PUT** `/restaurants/{restaurantId}`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Update restaurant details
- **Request Body**:
```json
{
  "name": "Pizza Palace Deluxe",
  "address": "789 Food Street, Bangalore",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "fssaiLicense": "12345678901234"
}
```

##### 3.1.3 Get Owner's Restaurants
**GET** `/restaurants/owner`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Get all restaurants owned by current user

##### 3.1.4 Toggle Restaurant Status
**PUT** `/restaurants/{restaurantId}/status`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Toggle restaurant open/closed status

##### 3.1.5 Get Restaurant Location
**GET** `/restaurants/{restaurantId}/location`
- **Auth Required**: No
- **Description**: Get restaurant address and coordinates

#### 3.2 Menu Item Management

##### 3.2.1 Add Menu Item
**POST** `/restaurants/{restaurantId}/items`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Add a new menu item
- **Request Body**:
```json
{
  "name": "Margherita Pizza",
  "description": "Classic cheese pizza with tomato sauce",
  "isVeg": true,
  "price": 299.00
}
```

##### 3.2.2 Update Menu Item
**PUT** `/restaurants/{restaurantId}/items/{itemId}`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Update menu item details
- **Request Body**:
```json
{
  "name": "Margherita Pizza Special",
  "description": "Classic cheese pizza with extra cheese",
  "isVeg": true,
  "price": 349.00
}
```

##### 3.2.3 Delete Menu Item
**DELETE** `/restaurants/{restaurantId}/items/{itemId}`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Remove a menu item

##### 3.2.4 Get Items by Restaurant
**GET** `/items/restaurants/{restaurantId}`
- **Auth Required**: No
- **Description**: Get all menu items for a restaurant

##### 3.2.5 Search Items
**GET** `/items?name={itemName}&latitude={lat}&longitude={lon}`
- **Auth Required**: No
- **Description**: Search for items by name near a location
- **Query Parameters**:
    - `name`: Item name to search
    - `latitude`: User's latitude
    - `longitude`: User's longitude
- **Example**: `/items?name=pizza&latitude=12.9716&longitude=77.5946`

##### 3.2.6 Get Item Details
**GET** `/items/{itemId}`
- **Auth Required**: No
- **Description**: Get detailed information about a specific item

#### 3.3 Restaurant Schedule

##### 3.3.1 Create/Update Schedule
**POST** `/restaurants/{restaurantId}/schedule`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Set operating hours for a day
- **Request Body**:
```json
{
  "weekDay": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "22:00:00"
}
```

##### 3.3.2 Get Restaurant Schedule
**GET** `/restaurants/{restaurantId}/schedule`
- **Auth Required**: Yes (RESTAURANT_OWNER)
- **Description**: Get all operating hours

---

### 4. Order Service

#### 4.1 Create Order
**POST** `/orders/{addressId}`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Place an order from cart items
- **Path Parameter**: `addressId` - Delivery address ID
- **Process**:
    1. Fetches cart items
    2. Groups items by restaurant
    3. Calculates pricing (items, tax, delivery fee, platform fee)
    4. Creates order(s)
    5. Publishes order event to Kafka

#### 4.2 Confirm Order
**POST** `/orders/{orderId}/confirm`
- **Auth Required**: Yes (CUSTOMER)
- **Description**: Confirm order after payment
- **Process**:
    1. Updates order status to CONFIRMED
    2. Publishes ORDER_CONFIRMED event to Kafka
    3. Triggers rider dispatch

#### 4.3 Assign Rider (Internal)
**PUT** `/orders/{orderId}/assign-rider?riderId={riderId}`
- **Auth Required**: Internal (Called by Dispatch Service)
- **Description**: Assign a rider to an order

---

### 5. Payment Service

#### 5.1 Process Payment
**POST** `/payments/pay`
- **Auth Required**: Yes
- **Description**: Process payment for an order
- **Request Body**:
```json
{
  "orderId": "order-uuid",
  "amount": 450.00,
  "paymentMode": "UPI"
}
```
- **Process**:
    1. Creates payment record
    2. Processes payment (mock bank call)
    3. If successful, calls order service to confirm order
    4. Updates payment status

---

### 6. Dispatch Service

#### 6.1 Update Rider Location
**POST** `/dispatch/riders/{riderId}/location?lat={latitude}&lon={longitude}`
- **Auth Required**: Yes (RIDER)
- **Description**: Update rider's current location
- **Query Parameters**:
    - `lat`: Latitude
    - `lon`: Longitude
- **Process**: Stores location in Redis using geospatial data

#### 6.2 Kafka Event Listener (Background)
- **Topic**: `order-events`
- **Event**: `ORDER_CONFIRMED`
- **Process**:
    1. Finds nearest available rider within 3km
    2. Assigns rider to order
    3. Calls order service to update order with rider ID

---

## Data Models

### Order Pricing Structure
```
Total Item Amount: Sum of (item price * quantity)
Tax: 5% of total item amount
Delivery Fee: ₹30.00 (fixed)
Platform Fee: ₹5.00 (fixed)
Final Amount: Total Item Amount + Tax + Delivery Fee + Platform Fee
```

### Order Status Flow
```
CREATED → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
         ↓
      CANCELLED
```

### Payment Status
```
PENDING → SUCCESS
        ↓
       FAILED
```

---

## Kafka Topics

### order-events
- **Producers**: Order Service
- **Consumers**: Dispatch Service
- **Events**:
    - `ORDER_CREATED`: When order is first created
    - `ORDER_CONFIRMED`: When payment is successful

---

## Redis Usage

### Dispatch Service
- **Key**: `available_riders`
- **Type**: Geospatial (GEO)
- **Purpose**: Store and query rider locations
- **Operations**:
    - `GEOADD`: Add/update rider location
    - `GEORADIUS`: Find riders within radius

---

## Error Handling

All services return standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### Common HTTP Status Codes
- `200 OK`: Successful request
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid input
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

---

## Environment Variables

Required environment variables (stored in `.env` file):

```properties
ACCESS_TOKEN_SECRET=your-access-token-secret-min-256-bits
REFRESH_TOKEN_SECRET=your-refresh-token-secret-min-256-bits
ACCESS_TOKEN_EXPIRATION=900000
REFRESH_TOKEN_EXPIRATION=604800000
```

---

## Database Schemas

### Databases
- `auth_db`: Identity Service (Port 5435)
- `user_db`: User Service (Port 5435)
- `restaurant_db`: Restaurant Service (Port 5435)
- `order_db`: Order Service (Port 5435)
- `payment_db`: Payment Service (Port 5435)

### PostGIS Extension
Restaurant and User services use PostGIS for geospatial queries:
- Restaurant location storage
- Distance calculations
- Radius-based searches

---

## Testing the APIs

### Prerequisites
1. Start Eureka Server (Port 8761)
2. Start Gateway Service (Port 8090)
3. Start required microservices
4. Start PostgreSQL (Port 5435)
5. Start Kafka (Port 9092)
6. Start Redis (Port 6379)

### Example Flow: Customer Orders Food

1. **Register & Login**
```bash
# Register
curl -X POST http://localhost:8090/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@test.com","password":"test123","role":"CUSTOMER"}'

# Login
curl -X POST http://localhost:8090/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@test.com","password":"test123"}' \
  -c cookies.txt
```

2. **Create User Profile**
```bash
curl -X POST http://localhost:8090/users \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"name":"John Customer","phoneNumber":"+919876543210"}'
```

3. **Add Delivery Address**
```bash
curl -X POST http://localhost:8090/customers/{customerId}/addresses \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"latitude":12.9716,"longitude":77.5946,"address":"123 MG Road, Bangalore"}'
```

4. **Search for Food**
```bash
curl "http://localhost:8090/items?name=pizza&latitude=12.9716&longitude=77.5946" \
  -b cookies.txt
```

5. **Add Items to Cart**
```bash
curl -X POST http://localhost:8090/carts/add \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"itemId":"item-uuid","quantity":2}'
```

6. **Place Order**
```bash
curl -X POST http://localhost:8090/orders/{addressId} \
  -b cookies.txt
```

7. **Make Payment**
```bash
curl -X POST http://localhost:8090/payments/pay \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"orderId":"order-uuid","amount":450.00,"paymentMode":"UPI"}'
```

---

## Notes

- All monetary values are in INR (₹)
- Distances are in meters (API) and kilometers (responses)
- Coordinates use WGS84 (SRID 4326)
- Times are in IST (Asia/Kolkata)
- UUIDs are used for all entity IDs
- Passwords are hashed using BCrypt
- JWT tokens expire after 15 minutes (access) and 7 days (refresh)