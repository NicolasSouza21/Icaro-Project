import { useAuth } from './context/AuthContext';
import './App.css';

import { BrowserRouter, Routes, Route, Navigate, Outlet, Link } from 'react-router-dom';

import LoginPage from './pages/LoginPage';
import ProfessorDashboardPage from './pages/ProfessorDashboardPage';
import CadastroAlunoPage from './pages/CadastroAlunoPage';

import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
// ✨ ALTERAÇÃO AQUI: Importa um ícone para o título
import SchoolIcon from '@mui/icons-material/School';


// --- Componente Auxiliar para Rotas Protegidas (inalterado) ---
function ProtectedRoute() {
  const { logado, loading } = useAuth();
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }
  if (!logado) {
    return <Navigate to="/login" replace />;
  }
  return <Outlet />;
}


// --- ✨ ALTERAÇÃO AQUI: Layout principal com a Navbar refatorada ---
function MainLayout() {
    const { logout, usuario } = useAuth();
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <AppBar position="static">
                <Toolbar>
                    {/* 1. Título Fixo do Projeto */}
                    <SchoolIcon sx={{ mr: 1 }} />
                    <Typography variant="h6" component="div">
                        Projeto ÍCARO
                    </Typography>

                    {/* 2. Caixa com os Links de Navegação */}
                    <Box sx={{ flexGrow: 1, ml: 2 }}>
                        <Button color="inherit" component={Link} to="/">
                            Dashboard
                        </Button>
                        <Button color="inherit" component={Link} to="/cadastrar-aluno">
                            Cadastrar Aluno
                        </Button>
                        {/* Adicione novos links aqui no futuro */}
                    </Box>

                    {/* 3. Informações do Usuário e Botão de Sair */}
                    <Typography sx={{ mr: 2, display: { xs: 'none', sm: 'block' } }}>
                        {usuario?.email}
                    </Typography>
                    <Button color="inherit" onClick={logout}>
                        Sair
                    </Button>
                </Toolbar>
            </AppBar>
            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <Outlet />
            </Box>
        </Box>
    );
}

// --- Componente Principal App (inalterado) ---
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
            <Route element={<MainLayout />}>
                <Route path="/" element={<HomeDashboard />} />
                <Route path="/cadastrar-aluno" element={<CadastroAlunoPage />} />
            </Route>
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}


// --- Componente Auxiliar para Roteamento por Role (inalterado) ---
function HomeDashboard() {
    const { usuario, logado } = useAuth();
    if (logado && usuario) {
        if (usuario.role === 'PROFESSOR') {
            return <ProfessorDashboardPage />;
        }
        else {
             return (
                <Box sx={{ p: 3 }}>
                    <Typography variant="h5" color="error">Erro</Typography>
                    <Typography>Tipo de usuário ({usuario.role}) não reconhecido.</Typography>
                </Box>
            );
        }
    }
    return null;
}

export default App;