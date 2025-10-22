// ✨ Imports do React e da instância 'api.js'
import React, { createContext, useState, useContext, useEffect } from "react";
import api from "../services/api"; // A instância do Axios que criamos

// 1. Criação do Contexto
const AuthContext = createContext({});

// 2. Provedor (Provider)
export const AuthProvider = ({ children }) => {
  // 3. Estados globais de autenticação
  const [usuario, setUsuario] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true); // Para verificar localStorage

  // 4. Carregar dados do localStorage (persistência)
  useEffect(() => {
    async function carregarDadosStorage() {
      const storageToken = localStorage.getItem("@Icaro:token");
      const storageUsuario = localStorage.getItem("@Icaro:usuario");

      if (storageToken && storageUsuario) {
        // ✅ Sem barra invertida — apenas crase
        api.defaults.headers.common["Authorization"] = `Bearer ${storageToken}`;

        setToken(storageToken);
        setUsuario(JSON.parse(storageUsuario));
      }

      setLoading(false); // Termina o carregamento
    }

    carregarDadosStorage();
  }, []); // Executa apenas na montagem

  // 5. Função de Login
  const login = async (email, senha) => {
    try {
      const response = await api.post("/api/v1/auth/login", { email, senha });

      const { token: novoToken, email: emailUsuario, role } = response.data;

      // ✅ Define o header global
      api.defaults.headers.common["Authorization"] = `Bearer ${novoToken}`;

      // ✅ Armazena os dados no localStorage
      localStorage.setItem("@Icaro:token", novoToken);
      localStorage.setItem(
        "@Icaro:usuario",
        JSON.stringify({ email: emailUsuario, role })
      );

      // ✅ Atualiza o estado global
      setToken(novoToken);
      setUsuario({ email: emailUsuario, role });
    } catch (error) {
      console.error("Erro no login:", error.response?.data || error.message);
      throw new Error(error.response?.data?.message || "Erro ao tentar logar");
    }
  };

  // 6. Função de Logout
  const logout = () => {
    localStorage.removeItem("@Icaro:token");
    localStorage.removeItem("@Icaro:usuario");

    setToken(null);
    setUsuario(null);

    delete api.defaults.headers.common["Authorization"];
  };

  // 7. Retorna o provedor do contexto
  return (
    <AuthContext.Provider
      value={{
        logado: !!usuario,
        usuario,
        token,
        loading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// 8. Hook customizado para acessar o contexto
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve ser usado dentro de um AuthProvider.");
  }
  return context;
};
