import DashboardPage from './pages/DashboardPage'
import AuthTokenBridge from './auth/AuthTokenBridge'
import AuthGate from './auth/AuthGate'

export default function App() {
  return (
    <>
      <AuthTokenBridge />
      <AuthGate>
        <DashboardPage />
      </AuthGate>
    </>
  )
}