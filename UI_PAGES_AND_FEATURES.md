# Adahi UI Pages and Features

## Overview

Adahi is an Arabic, right-to-left Android ordering app for browsing sacrificial animals, checking details, placing an order, and tracking existing orders. The UI is organized as a simple flow:

1. Launch the app.
2. Browse available animals.
3. Open an animal detail page.
4. Complete the order form.
5. Review the confirmation screen.
6. Track saved orders from the tracking page.

## Pages

| Page | Purpose | Main Features |
| --- | --- | --- |
| Main screen | Entry/loading screen | Shows the app branding and a loading message while the app routes to the main browsing experience. |
| Animal list | Browse available animals | Category chips, location selectors, price filters, featured offers list, animal cards, loading indicator, and a shortcut to orders. |
| Animal detail | View one animal in detail | Hero image, image gallery strip, veterinary badge, animal name, type, weight, price, breed, age, gender, and sales-point information. |
| Order form | Collect customer and order data | Customer name, phone, national ID, wilaya selection, quantity display, fee summary, confirm button, and cancel button. |
| Order confirmation | Confirm payment and order details | Order ID, status, payment summary, payment action buttons, next-step guidance, animal summary, customer details, and return/back actions. |
| Order tracking | Review active orders | Orders count, loading state, empty state, order cards, and actions for viewing or deleting tracked orders. |

## Page Details

### Main Screen

The launcher screen is minimal and acts like a branded splash/loading state. It uses a centered card with the app name and a short loading message. Its role is to transition the user into the main browsing flow.

### Animal List

This is the core browsing page. It presents available animals in a vertically scrolling layout with:

- Wilaya and sales-point dropdowns.
- Category chips for All, Sheep, Cows, Goats, and Camels.
- Price range filter chips.
- A RecyclerView of featured animal cards.
- A progress indicator while data is loading.
- Informational cards that highlight safety and secure payment.

Each animal card shows an image, badge, name, type, weight, location, description, price, and a primary action button for opening or reserving the animal.

### Animal Detail

The detail page focuses on one selected animal and gives the user enough information to make a purchase decision. It includes:

- A large hero image with a verified veterinary badge.
- A horizontal gallery of additional images.
- Title, type, weight, and price.
- A specifications section with breed, age, gender, and sales point.
- Additional detail cards below the fold for richer context and decision support.

### Order Form

The order form collects the information needed to place the reservation or purchase. It includes:

- Customer name input.
- Customer phone input.
- National ID input.
- Wilaya selector.
- Quantity display.
- Fee summary for the current order type.
- A fixed bottom action area with confirm and cancel buttons.

The form is structured to keep the selected animal visible while the user enters their details, which helps reduce mistakes during checkout.

### Order Confirmation

The confirmation page summarizes the order after submission. It shows:

- Order ID and current status.
- Payment amount and payment method options.
- Step-by-step next actions after payment.
- The chosen animal’s key details.
- Buyer information and pickup or sales-point information.
- A final action to cancel the order when needed.

This page acts as both a receipt and a status checkpoint.

### Order Tracking

The tracking page is used to monitor saved orders. It supports:

- A top bar with the current order count.
- A loading state while orders are fetched.
- An empty state when no tracked orders exist.
- Order summary cards that show order ID, animal name, order type, date, fee, and status.
- Actions to view details or delete an order.

## Shared UI Patterns

- Right-to-left layout across the app.
- Material Design cards and buttons.
- Rounded chips and badges for filters and status labels.
- RecyclerView-based lists for scalable browsing and tracking.
- Clear primary action buttons for each step in the flow.
- Neutral loading and empty states to make progress and absence of data obvious.

## User Flow Summary

The UI is designed around a simple commerce journey. The user starts on the animal list, filters by location or category, opens a detailed animal page, fills the order form, reviews the confirmation screen, and later checks the tracking page for saved orders and status updates.