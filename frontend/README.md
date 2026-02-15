# FinMate AI - Frontend

A modern React + TypeScript frontend application for managing personal finances with AI-powered insights.

## Technology Stack

- **React 18** - UI library
- **TypeScript** - Type-safe JavaScript
- **Vite** - Fast build tool
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Supabase** - Backend as a Service (Authentication & Database)
- **Recharts** - Data visualization
- **i18next** - Internationalization (English, Tamil, Hindi)
- **Axios** - HTTP client
- **Lucide React** - Icon library

## Features

### 1. Authentication
- User registration and login
- JWT-based authentication with Supabase
- Protected routes
- Session management

### 2. Dashboard
- Overview of financial status
- Total income, expenses, and balance
- Budget progress tracking
- Category-wise expense breakdown (Pie Chart)
- Monthly income/expense trends (Bar Chart)

### 3. Transaction Management
- Add, edit, and delete transactions
- Income and expense tracking
- Category-based organization
- Filter by category and month
- Date-based transaction history

### 4. Budget Management
- Set monthly budget limits
- Real-time spending tracking
- Budget progress visualization
- Overspending alerts and warnings
- Budget vs actual comparison

### 5. AI Assistant
- Chat interface for financial queries
- Real-time AI responses
- Chat history
- Natural language processing

### 6. Profile Management
- Update user information
- Multi-language support (English, Tamil, Hindi)
- Dark/Light theme toggle
- Preferences management

## Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── Card.tsx
│   │   ├── EmptyState.tsx
│   │   ├── Layout.tsx
│   │   ├── Loading.tsx
│   │   └── Modal.tsx
│   ├── context/            # React Context providers
│   │   ├── AuthContext.tsx
│   │   └── ThemeContext.tsx
│   ├── i18n/               # Internationalization
│   │   ├── config.ts
│   │   └── locales/
│   │       ├── en.json
│   │       ├── hi.json
│   │       └── ta.json
│   ├── pages/              # Page components
│   │   ├── AIChat.tsx
│   │   ├── Budget.tsx
│   │   ├── Dashboard.tsx
│   │   ├── Login.tsx
│   │   ├── Profile.tsx
│   │   ├── Signup.tsx
│   │   └── Transactions.tsx
│   ├── services/           # API services
│   │   └── supabase.ts
│   ├── App.tsx             # Main app component
│   ├── main.tsx            # Entry point
│   └── index.css           # Global styles
├── .env                    # Environment variables
├── index.html              # HTML template
├── package.json            # Dependencies
├── tailwind.config.js      # Tailwind configuration
├── tsconfig.json           # TypeScript configuration
└── vite.config.ts          # Vite configuration
```

## Prerequisites

- Node.js 18+ and npm
- Supabase account (for authentication and database)

## Installation & Setup

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Environment Configuration

The `.env` file is already configured with Supabase credentials:

```env
VITE_SUPABASE_URL=https://qfiqdjsxudeygcfgwkju.supabase.co
VITE_SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Run Development Server

```bash
npm run dev
```

Or use the provided batch file:
```bash
start.bat
```

The application will start on `http://localhost:5173`

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint
- `npm run typecheck` - Run TypeScript type checking

## Features Overview

### Authentication Flow
1. User registers with email and password
2. Supabase creates authentication record
3. User profile is stored in `users` table
4. JWT token is managed by Supabase
5. Protected routes check authentication status

### Dashboard Analytics
- Real-time financial overview
- Visual charts for spending patterns
- Budget progress indicators
- Category-wise expense breakdown
- Monthly trend analysis

### Transaction Management
- Quick add transaction modal
- Edit existing transactions
- Delete with confirmation
- Filter by category and date
- Responsive table view

### Budget Tracking
- Set monthly budget limits
- Visual progress bar
- Color-coded alerts (green/yellow/red)
- Overspending notifications
- Budget vs actual comparison

### AI Chat Integration
- Connect to backend AI service at `http://localhost:8080`
- Real-time chat interface
- Message history
- Natural language queries

### Multi-language Support
- English (default)
- Tamil (தமிழ்)
- Hindi (हिन्दी)
- Automatic language detection
- Persistent language preference

### Theme Support
- Light mode (default)
- Dark mode
- System preference detection
- Persistent theme preference
- Smooth transitions

## Supabase Database Schema

### Users Table
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  type TEXT NOT NULL, -- 'income' or 'expense'
  amount NUMERIC NOT NULL,
  category TEXT NOT NULL,
  description TEXT,
  date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);
```

### Budgets Table
```sql
CREATE TABLE budgets (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  month TEXT NOT NULL, -- Format: 'YYYY-MM'
  amount NUMERIC NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(user_id, month)
);
```

## API Integration

### Backend Connection
The AI Chat feature connects to the backend API:
- Base URL: `http://localhost:8080`
- Endpoint: `/api/ai/chat`
- Method: POST
- Body: `{ "message": "user query" }`

Make sure the backend server is running for AI chat functionality.

## Styling

### Tailwind CSS
- Utility-first CSS framework
- Custom color palette
- Responsive design
- Dark mode support
- Custom animations

### Design System
- Primary color: Blue (#3B82F6)
- Success color: Green (#10B981)
- Warning color: Yellow (#F59E0B)
- Error color: Red (#EF4444)
- Neutral grays for backgrounds

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Production Build

### Build the application
```bash
npm run build
```

This creates an optimized production build in the `dist/` folder.

### Preview production build
```bash
npm run preview
```

### Deploy
The `dist/` folder can be deployed to:
- Vercel
- Netlify
- AWS S3 + CloudFront
- Any static hosting service

## Troubleshooting

### Port already in use
If port 5173 is already in use, Vite will automatically use the next available port.

### Supabase connection issues
- Check `.env` file has correct credentials
- Verify Supabase project is active
- Check network connectivity

### Build errors
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### TypeScript errors
```bash
# Run type checking
npm run typecheck
```

## Development Tips

1. **Hot Module Replacement**: Vite provides instant HMR for fast development
2. **TypeScript**: Use proper types for better IDE support
3. **Component Structure**: Keep components small and reusable
4. **State Management**: Use Context API for global state
5. **Error Handling**: Always handle API errors gracefully

## Contributing

1. Follow the existing code style
2. Use TypeScript for type safety
3. Write meaningful commit messages
4. Test thoroughly before committing

## License

Copyright © 2024 FinMate AI. All rights reserved.
