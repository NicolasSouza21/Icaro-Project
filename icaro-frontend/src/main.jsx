import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css'; 
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx';

import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

// --- ✨ ALTERAÇÃO AQUI: Define a paleta de cores UNIP ---
const theme = createTheme({
  palette: {
    primary: {
      // Azul UNIP (aproximado)
      main: '#005AAB', // Um azul corporativo comum, similar ao da imagem
      contrastText: '#FFFFFF', // Texto branco sobre o azul
    },
    secondary: {
      // Amarelo UNIP (aproximado)
      main: '#FFD100', // Um amarelo vibrante, similar ao logo
      contrastText: '#333333', // Texto escuro sobre o amarelo para contraste
    },
    // Você pode definir outras cores como error, warning, info, success
    // background: { default: '#f4f6f8' } // Ex: Fundo levemente cinza
  },
  // Você também pode customizar tipografia, espaçamento, etc. aqui
  // typography: {
  //   fontFamily: 'Roboto, Arial, sans-serif',
  // },
});

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <App />
      </AuthProvider>
    </ThemeProvider>
  </StrictMode>,
);