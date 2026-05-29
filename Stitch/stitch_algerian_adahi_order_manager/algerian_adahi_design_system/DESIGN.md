---
name: Algerian Adahi Design System
colors:
  surface: '#faf9f6'
  surface-dim: '#dbdad7'
  surface-bright: '#faf9f6'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f4f3f1'
  surface-container: '#efeeeb'
  surface-container-high: '#e9e8e5'
  surface-container-highest: '#e3e2e0'
  on-surface: '#1a1c1a'
  on-surface-variant: '#42493e'
  inverse-surface: '#2f312f'
  inverse-on-surface: '#f2f1ee'
  outline: '#72796e'
  outline-variant: '#c2c9bb'
  surface-tint: '#3b6934'
  primary: '#154212'
  on-primary: '#ffffff'
  primary-container: '#2d5a27'
  on-primary-container: '#9dd090'
  inverse-primary: '#a1d494'
  secondary: '#735c00'
  on-secondary: '#ffffff'
  secondary-container: '#fed65b'
  on-secondary-container: '#745c00'
  tertiary: '#6b5d35'
  on-tertiary: '#ffffff'
  tertiary-container: '#bcaa7b'
  on-tertiary-container: '#4b3e19'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#bcf0ae'
  primary-fixed-dim: '#a1d494'
  on-primary-fixed: '#002201'
  on-primary-fixed-variant: '#23501e'
  secondary-fixed: '#ffe088'
  secondary-fixed-dim: '#e9c349'
  on-secondary-fixed: '#241a00'
  on-secondary-fixed-variant: '#574500'
  tertiary-fixed: '#f5e1ae'
  tertiary-fixed-dim: '#d8c594'
  on-tertiary-fixed: '#241a00'
  on-tertiary-fixed-variant: '#524620'
  background: '#faf9f6'
  on-background: '#1a1c1a'
  surface-variant: '#e3e2e0'
typography:
  headline-xl:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 52px
  headline-lg:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.02em
  price-display:
    fontFamily: IBM Plex Sans Arabic
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  gutter: 16px
  margin-mobile: 20px
  margin-desktop: 64px
  container-max: 1280px
---

## Brand & Style

The design system is crafted to facilitate a sacred and significant cultural transaction: the purchase of Adahi for Eid Al-Adha. The brand personality is **Trustworthy, Devotional, and Premium**. It bridges the gap between traditional livestock trading and modern e-commerce convenience.

The visual style is **Corporate / Modern with Subtle Tactile accents**. It prioritizes high legibility and a sense of "Amana" (trust). We utilize a clean, structured layout with plenty of white space to ensure the application feels organized and accessible to all age groups across Algeria. The aesthetic leans into a "Modern Heritage" feel—using contemporary UI patterns while respecting the religious and cultural weight of the service. 

Key principles:
- **RTL-First:** Every layout decision is made with Right-to-Left Arabic script as the primary orientation.
- **Transparency:** Clear pricing in DZD and detailed livestock specifications to build user confidence.
- **Warmth:** Using organic color tones to avoid the coldness of typical fintech applications.

## Colors

The palette is inspired by the Algerian landscape and Islamic tradition.

- **Primary (Deep Moss Green):** Represents growth, life, and the traditional color of Islam. Used for primary actions, success states, and brand headers.
- **Secondary (Desert Gold):** Represents quality, the sun, and the value of the sacrifice. Used for highlights, badges, and premium features.
- **Tertiary (Earthy Umber):** A grounding color used for secondary information and iconography related to the livestock's origin.
- **Neutral (Bone/Sand):** A soft, warm off-white that reduces eye strain and provides a premium feel compared to pure white.

Text colors should utilize deep charcoal (#1A1A1A) for maximum contrast against the neutral background, ensuring accessibility for older users.

## Typography

The design system utilizes **IBM Plex Sans Arabic** for its exceptional clarity and professional tone. This font family provides a perfect balance between technical precision and calligraphic heritage, making it ideal for a trust-based application.

**Usage Rules:**
- All text is right-aligned by default.
- **Price Display:** Prices in DZD should be emphasized using the `price-display` style, ensuring the currency symbol is legible.
- **Hierarchy:** Use weight (Bold/Medium) rather than just size to distinguish between headers and body text, as Arabic script can often appear smaller than Latin script at the same point size.
- **Line Height:** Given the height of Arabic characters (ascenders/descenders), line heights are more generous than standard Latin-based systems to prevent clashing.

## Layout & Spacing

The layout follows a **Fluid Grid** model with a base-8 spacing system.

- **Grid:** A 12-column grid is used for desktop, 6-column for tablet, and a single-column stack for mobile.
- **RTL Directionality:** Navigation flows from right to left. Sidebars appear on the right, and "back" arrows point to the right.
- **Spacing Rhythm:** Use `16px` (2 units) for internal component padding and `24px` (3 units) for spacing between distinct content sections.
- **Mobile Comfort:** Large touch targets are prioritized, with a minimum height of `48px` for all interactive elements.

## Elevation & Depth

The design system uses **Tonal Layers** combined with **Ambient Shadows** to create a sense of physical space.

- **Level 0 (Surface):** The Neutral background (#FAF9F6).
- **Level 1 (Cards):** Pure white (#FFFFFF) with a very soft, diffused shadow (0px 4px 20px rgba(45, 90, 39, 0.05)). This depth is used for livestock listings.
- **Level 2 (Modals/Dropdowns):** Elevated surfaces with a more pronounced shadow to indicate focus.
- **Interactive Depth:** Buttons use a subtle inner-glow on hover rather than heavy shadows to maintain a modern, clean feel.

We avoid heavy "skeuomorphism" but use high-quality photography of livestock to provide the necessary realism and connection to the physical product.

## Shapes

The design system employs **Rounded** corners to evoke a sense of friendliness and accessibility.

- **Base Radius:** `0.5rem` (8px) for standard buttons, input fields, and small cards.
- **Large Radius:** `1rem` (16px) for main product cards and container sections.
- **Full Radius (Pill):** Used exclusively for status badges (e.g., "Available", "Sold Out") and category filters.

Soft edges help the application feel less like a rigid financial tool and more like a helpful community service.

## Components

### Buttons
- **Primary:** Solid Primary Green (#2D5A27) with white text. High contrast, used for "Confirm Order" or "Add to Cart".
- **Secondary:** Outlined in Secondary Gold (#D4AF37) with Gold text. Used for "View Details" or "Compare".
- **Tertiary:** Text-only, used for "Cancel" or "Go Back".

### Cards (Livestock)
Livestock cards are the core component. They must feature:
- A high-resolution image of the sheep/goat.
- A prominent price tag in DZD at the bottom-right (RTL).
- Attribute chips (Weight, Age, Breed) using the Tertiary color.

### Input Fields
- Labels are always positioned above the field, right-aligned.
- Focus states use a 2px Primary Green border.
- Error states use a soft red, but must be accompanied by an icon for clarity.

### Progress Stepper
For the ordering process (Selection > Payment > Delivery/Slaughter), use a horizontal stepper that flows **Right to Left**. Completed steps are marked in Primary Green with a checkmark.

### DZD Price Display
Always format the currency as: `120.000 د.ج` or `120.000 DZD` depending on the user's language toggle, ensuring the thousand separator is used for readability.