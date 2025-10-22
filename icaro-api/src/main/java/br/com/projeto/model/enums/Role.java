package br.com.projeto.model.enums;

// ✨ ALTERAÇÃO AQUI: Mudamos de "class" para "enum" (Enumeração)
public enum Role {
    
    // ✨ ALTERAÇÃO AQUI: Definimos os papéis (níveis de acesso) fixos do sistema ÍCARO
    ALUNO,
    PROFESSOR,
    ADMIN
    
    // ✨ ALTERAÇÃO AQUI: Adicionamos um papel para o sistema de Totem (opcional, mas boa prática)
    // Isso permite que o Totem se autentique na API de forma segura, sem ser um "Admin"
    // TOTEM 
}