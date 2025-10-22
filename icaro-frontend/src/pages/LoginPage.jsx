import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Navigate } from 'react-router-dom';

// ✨ ALTERAÇÃO AQUI: Imports do Material UI
import { Alert, Box, Button, CircularProgress, Container, Paper, TextField, Typography } from '@mui/material';
import LoginIcon from '@mui/icons-material/Login'; // Ícone de Login

function LoginPage() {
    const { login, logado, loading } = useAuth();
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [erroLogin, setErroLogin] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setErroLogin('');
        try {
            await login(email, senha);
            navigate('/'); // Redireciona para o dashboard após login
        } catch (error) {
            console.error('Falha no login:', error);
            setErroLogin(error.message || 'E-mail ou senha inválidos.');
        }
    };

    // Se já estiver logado (e não carregando), redireciona para a raiz
    if (!loading && logado) {
        return <Navigate to="/" replace />;
    }

    // Mostra loading se o AuthContext ainda está verificando
    if (loading) {
        return (
             <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <CircularProgress />
            </Box>
        );
     }

    // Renderiza o formulário de login com MUI
    return (
         // Container centraliza o conteúdo horizontalmente
        <Container component="main" maxWidth="xs" sx={{ display: 'flex', alignItems: 'center', height: '100vh' }}>
             {/* Paper cria o card com sombra */}
            <Paper elevation={4} sx={{ p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}> {/* p=padding */}
                <Typography component="h1" variant="h5" sx={{ mb: 1 }}> {/* mb=margin-bottom */}
                    Projeto ÍCARO - Login
                </Typography>
                {/* Box para o formulário */}
                <Box component="form" onSubmit={handleLogin} sx={{ mt: 1, width: '100%' }}> {/* mt=margin-top */}
                     {/* Campo de Email */}
                    <TextField
                        margin="normal" // Adiciona espaçamento vertical
                        required
                        fullWidth
                        id="email"
                        label="Endereço de E-mail"
                        name="email"
                        autoComplete="email"
                        autoFocus // Foca neste campo ao carregar
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                     {/* Campo de Senha */}
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="senha"
                        label="Senha"
                        type="password"
                        id="senha"
                        autoComplete="current-password"
                        value={senha}
                        onChange={(e) => setSenha(e.target.value)}
                    />
                    {/* Botão de Entrar */}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained" // Usa a cor primária (azul)
                        sx={{ mt: 3, mb: 2 }} // Margens verticais
                        startIcon={<LoginIcon />} // Adiciona ícone
                    >
                        Entrar
                    </Button>
                    {/* Mensagem de Erro */}
                    {erroLogin && (
                        <Alert severity="error" sx={{ width: '100%', mt: 1 }}>
                            {erroLogin}
                        </Alert>
                    )}
                </Box>
            </Paper>
        </Container>
    );
}

export default LoginPage;