// ✨ ALTERAÇÃO AQUI: Importa o axios que acabamos de instalar
import axios from "axios";

// ✨ ALTERAÇÃO AQUI: Cria a instância do Axios
const api = axios.create({
    
    // 1. Define a URL base para todas as requisições
    // Agora, em vez de chamar axios.post('http://localhost:8080/api/v1/auth/login'),
    // podemos apenas chamar api.post('/api/v1/auth/login')
    baseURL: "http://localhost:8080",

    // 2. Permite o envio de credenciais (como cookies)
    // Isso é crucial para que o CORS do Spring Boot (que configuramos com 
    // .setAllowCredentials(true)) funcione corretamente, mesmo que 
    // estejamos usando JWT em vez de cookies de sessão.
    withCredentials: true 
});

// ✨ ALTERAÇÃO AQUI: Exporta a instância configurada
export default api;