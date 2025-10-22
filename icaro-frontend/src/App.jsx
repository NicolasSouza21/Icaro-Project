import { useAuth } from './context/AuthContext';
import './App.css';

import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';

import LoginPage from './pages/LoginPage';
import ProfessorDashboardPage from './pages/ProfessorDashboardPage';

import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';

// --- Componente Auxiliar para Rotas Protegidas ---
function ProtectedRoute() {
  const { logado, loading } = useAuth();

  // Se ainda está a verificar o estado inicial (localStorage)
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  // ✨ ALTERAÇÃO AQUI: Se NÃO estiver logado, inicia o redirecionamento
  // e retorna null imediatamente para evitar renderizar o Outlet antigo.
  if (!logado) {
    // O componente Navigate vai tratar do redirecionamento na próxima renderização do router
    return <Navigate to="/login" replace />;
    // Retornar null aqui explicitamente pode não ser necessário se o Navigate funcionar como esperado,
    // mas vamos manter assim por enquanto como tentativa de correção.
    // Se o problema persistir, remover a linha abaixo pode ser um teste.
    // return null;
  }

  // Se passou pelas verificações (loading=false E logado=true), renderiza a rota filha
  return <Outlet />;
}


// --- Componente Auxiliar para Layout Principal ---
// (MainLayout permanece igual)
function MainLayout() {
    const { logout, usuario } = useAuth();
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        Projeto ÍCARO - {usuario?.role}
                    </Typography>
                    <Button color="inherit" onClick={logout}>
                        Sair (Logout)
                    </Button>
                </Toolbar>
            </AppBar>
            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <Outlet />
            </Box>
        </Box>
    );
}

// --- Componente Principal App ---
// (App permanece igual)
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
            <Route element={<MainLayout />}>
                <Route path="/" element={<HomeDashboard />} />
            </Route>
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}


// --- Componente Auxiliar para Roteamento por Role ---
// (HomeDashboard permanece igual, a verificação logado && usuario já existe)
function HomeDashboard() {
    const { usuario, logado } = useAuth();

    if (logado && usuario) {
        if (usuario.role === 'PROFESSOR') {
            return <ProfessorDashboardPage />;
        }
        // else if (usuario.role === 'ALUNO') { ... }
        else {
             return (
                <Box sx={{ p: 3 }}>
                    <Typography variant="h5" color="error">Erro</Typography>
                    <Typography>Tipo de usuário ({usuario.role}) não reconhecido.</Typography>
                </Box>
            );
        }
    }
    // Retorna null se não estiver logado ou usuario for null
    return null;
}

export default App;