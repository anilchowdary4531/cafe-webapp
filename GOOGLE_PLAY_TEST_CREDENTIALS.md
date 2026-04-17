# Google Play Console Test Credentials - Sunset Cafe App

## App Overview
Sunset Cafe is a QR-based table ordering system that allows customers to scan QR codes at their tables, browse the menu, place orders, and track order status in real-time.

## Test Credentials for Reviewers

### Phone Verification (OTP)
- **Test Phone Number**: `9999999999` (10 digits)
- **Demo OTP Code**: `123456` (displayed in the app for demo purposes)

### Table Selection
- **Test Table**: Select any table from "Table 1" to "Table 30"

### Menu Testing
- **Regular Items**: Add any items from the menu (Coffee, Snacks, Juice, etc.)
- **Restricted Items**: Try ordering cigarettes (requires manual approval)
- **Cart Functionality**: Add multiple items, adjust quantities, remove items

### Order Placement
1. Select a table
2. Add items to cart
3. Verify phone number with OTP
4. Place order
5. View order status

### Service Requests
- **Call Waiter**: Test the call waiter functionality
- **Request Bill**: Test bill request functionality

### Demo Mode Features
- The app runs in demo mode with local storage fallback
- OTP codes are displayed for testing purposes
- Orders are saved locally when offline

## Testing Instructions

1. **Launch the app** and select a table
2. **Browse menu** and add items to cart
3. **Phone verification**: Enter `9999999999` and use OTP `123456`
4. **Place order** and verify it appears in "My Orders"
5. **Test service requests** (Call Waiter, Request Bill)
6. **Test restricted items** (cigarettes) for manual approval flow

## Technical Notes
- App works offline with local storage
- Real-time sync when backend is available
- Responsive design for mobile devices
- PWA capabilities for installation

## Contact Information
For any issues during testing, please contact the developer.
