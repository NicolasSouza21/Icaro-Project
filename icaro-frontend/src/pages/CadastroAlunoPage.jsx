// src/pages/CadastroAlunoPage.jsx

import { useState, useEffect } from 'react';
import api from '../services/api';
import {
    Container, Paper, Typography, TextField, Button, Box, CircularProgress,
    Alert, FormControl, InputLabel, Select, MenuItem
} from '@mui/material';
import PersonAddIcon from '@mui/icons-material/PersonAdd';

function CadastroAlunoPage() {
    // Estados para o formulário
    const [nome, setNome] = useState('');
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [matriculaRa, setMatriculaRa] = useState('');
    const [turmaId, setTurmaId] = useState('');

    // Estados para carregar as turmas do professor
    const [turmas, setTurmas] = useState([]);
    const [loadingTurmas, setLoadingTurmas] = useState(false);

    // Estados para o feedback do envio do formulário
    const [loadingCadastro, setLoadingCadastro] = useState(false);
    const [erroCadastro, setErroCadastro] = useState('');
    const [sucessoCadastro, setSucessoCadastro] = useState('');

    // Busca as turmas do professor logado para preencher o dropdown
    useEffect(() => {
        const fetchTurmas = async () => {
            setLoadingTurmas(true);
            try {
                const response = await api.get('/api/v1/turmas/minhas');
                setTurmas(response.data);
            } catch (error) {
                console.error("Erro ao buscar turmas", error);
                setErroCadastro("Falha ao carregar a lista de turmas. Tente recarregar a página.");
            } finally {
                setLoadingTurmas(false);
            }
        };

        fetchTurmas();
    }, []); // Executa apenas uma vez quando o componente é montado

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoadingCadastro(true);
        setErroCadastro('');
        setSucessoCadastro('');

        const requestData = {
            nome,
            email,
            senha,
            matriculaRa,
            turmaId: Number(turmaId)
        };

        try {
            const response = await api.post('/api/v1/professor/alunos', requestData);
            setSucessoCadastro(response.data); // Pega a mensagem de sucesso da API
            // Limpa o formulário
            setNome('');
            setEmail('');
            setSenha('');
            setMatriculaRa('');
            setTurmaId('');
        } catch (error) {
            console.error("Erro ao cadastrar aluno:", error.response?.data || error.message);
            const errorMsg = error.response?.data?.message || error.response?.data || "Ocorreu um erro desconhecido.";
            setErroCadastro(errorMsg);
        } finally {
            setLoadingCadastro(false);
        }
    };

    return (
        <Container component="main" maxWidth="md">
            <Paper elevation={4} sx={{ p: 4, mt: 4 }}>
                <Typography component="h1" variant="h5" sx={{ mb: 3 }}>
                    Cadastrar e Matricular Novo Aluno
                </Typography>
                <Box component="form" onSubmit={handleSubmit}>
                    <TextField
                        label="Nome Completo do Aluno"
                        fullWidth required margin="normal"
                        value={nome} onChange={(e) => setNome(e.target.value)}
                    />
                    <TextField
                        label="E-mail do Aluno"
                        type="email"
                        fullWidth required margin="normal"
                        value={email} onChange={(e) => setEmail(e.target.value)}
                    />
                    <TextField
                        label="Senha Provisória"
                        type="password"
                        fullWidth required margin="normal"
                        value={senha} onChange={(e) => setSenha(e.target.value)}
                    />
                    <TextField
                        label="Matrícula (RA)"
                        fullWidth required margin="normal"
                        value={matriculaRa} onChange={(e) => setMatriculaRa(e.target.value)}
                    />
                    <FormControl fullWidth required margin="normal" disabled={loadingTurmas}>
                        <InputLabel id="select-turma-label">Atribuir à Turma</InputLabel>
                        <Select
                            labelId="select-turma-label"
                            id="select-turma"
                            value={turmaId}
                            label="Atribuir à Turma"
                            onChange={(e) => setTurmaId(e.target.value)}
                        >
                            {loadingTurmas ? (
                                <MenuItem disabled>Carregando turmas...</MenuItem>
                            ) : (
                                turmas.map((turma) => (
                                    <MenuItem key={turma.id} value={turma.id}>
                                        {turma.nomeTurma} ({turma.nomeDisciplina} - {turma.semestre})
                                    </MenuItem>
                                ))
                            )}
                        </Select>
                    </FormControl>

                    {erroCadastro && <Alert severity="error" sx={{ mt: 2 }}>{erroCadastro}</Alert>}
                    {sucessoCadastro && <Alert severity="success" sx={{ mt: 2 }}>{sucessoCadastro}</Alert>}

                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={loadingCadastro || loadingTurmas}
                        startIcon={loadingCadastro ? <CircularProgress size={20} color="inherit" /> : <PersonAddIcon />}
                    >
                        Cadastrar Aluno
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
}

export default CadastroAlunoPage;