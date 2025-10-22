import { useState, useEffect } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

import { Alert, Box, Button, CircularProgress, Container, FormControl, InputLabel, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ListAltIcon from '@mui/icons-material/ListAlt';
import CreateIcon from '@mui/icons-material/Create';

function ProfessorDashboardPage() {
    // Estado de visualização
    const [viewMode, setViewMode] = useState('list');

    // ... (outros estados e funções permanecem iguais) ...
    const [nomeTurma, setNomeTurma] = useState('');
    const [semestre, setSemestre] = useState('2025.2');
    const [disciplinaId, setDisciplinaId] = useState('');
    const [mensagemCriacao, setMensagemCriacao] = useState({ type: '', text: '' });
    const [turmas, setTurmas] = useState([]);
    const [loadingTurmas, setLoadingTurmas] = useState(false);
    const [erroListagemTurmas, setErroListagemTurmas] = useState('');
    const [disciplinas, setDisciplinas] = useState([]);
    const [loadingDisciplinas, setLoadingDisciplinas] = useState(false);
    const [erroListagemDisciplinas, setErroListagemDisciplinas] = useState('');
    const { usuario } = useAuth();

    const buscarMinhasTurmas = async () => { /* ... */
        setLoadingTurmas(true);
        setErroListagemTurmas('');
        try {
            const response = await api.get('/api/v1/turmas/minhas');
            setTurmas(response.data);
        } catch (error) {
            console.error('Erro ao buscar turmas:', error.response?.data || error.message);
            setErroListagemTurmas('Falha ao carregar a lista de turmas.');
        } finally {
            setLoadingTurmas(false);
        }
    };
    const buscarDisciplinas = async () => { /* ... */
        setLoadingDisciplinas(true);
        setErroListagemDisciplinas('');
        try {
            const response = await api.get('/api/v1/disciplinas');
            setDisciplinas(response.data);
            if (response.data.length > 0 && !disciplinaId) {
                 const initialId = viewMode === 'create' ? (disciplinaId || response.data[0].id) : (disciplinaId || response.data[0].id) ;
                 setDisciplinaId(initialId);
            }
        } catch (error) {
            console.error('Erro ao buscar disciplinas:', error.response?.data || error.message);
            setErroListagemDisciplinas('Falha ao carregar disciplinas.');
        } finally {
            setLoadingDisciplinas(false);
        }
    };

    useEffect(() => {
        buscarMinhasTurmas();
        buscarDisciplinas();
    }, []);

    const handleCriarTurma = async (e) => { /* ... */
        e.preventDefault();
        setMensagemCriacao({ type: '', text: '' });
        if (!disciplinaId) {
            setMensagemCriacao({ type: 'error', text: 'Por favor, selecione uma disciplina.' });
            return;
        }
        const turmaRequest = { nomeTurma, semestre, disciplinaId: Number(disciplinaId) };
        try {
            await api.post('/api/v1/turmas', turmaRequest);
            setNomeTurma('');
            setMensagemCriacao({ type: '', text: '' });
            await buscarMinhasTurmas();
            setViewMode('list');
        } catch (error) {
            console.error('Erro ao criar turma:', error.response?.data || error.message);
            setMensagemCriacao({ type: 'error', text: `Erro: ${error.response?.data?.message || 'Falha ao criar turma.'}` });
        }
     };

    // --- Renderização ---
    return (
        // ✨ ALTERAÇÃO AQUI: Removemos maxWidth="lg"
        <Container sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Dashboard do Professor
            </Typography>
            <Typography variant="subtitle1" gutterBottom sx={{ mb: 3 }}>
                Bem-vindo, {usuario?.email}!
            </Typography>

            {/* --- Visualização da Lista --- */}
            {viewMode === 'list' && (
                <Paper elevation={3} sx={{ p: 3 }}>
                    { /* ... Conteúdo da Lista (Box Título/Botão, Loading, Erro, Tabela) ... */}
                     <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <ListAltIcon />
                            <Typography variant="h6">Minhas Turmas</Typography>
                        </Box>
                        <Button
                            variant="contained"
                            startIcon={<AddCircleOutlineIcon />}
                            onClick={() => { setViewMode('create'); setMensagemCriacao({ type: '', text: '' }); }}
                        >
                            Criar Nova Turma
                        </Button>
                    </Box>
                    {loadingTurmas && <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}><CircularProgress /></Box>}
                    {erroListagemTurmas && <Alert severity="error" sx={{ mt: 2 }}>{erroListagemTurmas}</Alert>}
                    {!loadingTurmas && !erroListagemTurmas && (
                        <TableContainer component={Paper} elevation={1} sx={{ mt: 2 }}>
                            <Table sx={{ minWidth: 650 }} aria-label="tabela de turmas">
                                <TableHead sx={{ backgroundColor: 'action.hover' }}>
                                    <TableRow>
                                        <TableCell sx={{ fontWeight: 'bold' }}>ID</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Nome Turma</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Semestre</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Disciplina</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {turmas.length === 0 ? (
                                        <TableRow>
                                            <TableCell colSpan={4} align="center" sx={{ p: 4, fontStyle: 'italic' }}>Nenhuma turma encontrada.</TableCell>
                                        </TableRow>
                                    ) : (
                                        turmas.map((turma) => (
                                            <TableRow key={turma.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                                <TableCell>{turma.id}</TableCell>
                                                <TableCell>{turma.nomeTurma}</TableCell>
                                                <TableCell>{turma.semestre}</TableCell>
                                                <TableCell>{turma.nomeDisciplina} ({turma.codigoDisciplina})</TableCell>
                                            </TableRow>
                                        ))
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                </Paper>
            )}

            {/* --- Visualização do Formulário --- */}
            {viewMode === 'create' && (
                 <Paper elevation={3} sx={{ p: 3 }}>
                    { /* ... Conteúdo do Formulário (Box Título/Botão, Inputs, Select, Botão Criar, Mensagem) ... */}
                     <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                         <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <CreateIcon />
                            <Typography variant="h6">Criar Nova Turma</Typography>
                        </Box>
                        <Button
                            variant="outlined" color="secondary" startIcon={<ArrowBackIcon />}
                            onClick={() => setViewMode('list')}
                        >
                            Voltar para Lista
                        </Button>
                    </Box>
                    <Box component="form" onSubmit={handleCriarTurma} sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField
                           label="Nome da Turma (Ex: A)" variant="outlined" value={nomeTurma} onChange={(e) => setNomeTurma(e.target.value)} required fullWidth
                        />
                        <TextField
                            label="Semestre (Ex: 2025.2)" variant="outlined" value={semestre} onChange={(e) => setSemestre(e.target.value)} required fullWidth
                        />
                        <FormControl fullWidth required error={!!erroListagemDisciplinas || loadingDisciplinas}>
                            <InputLabel id="disciplina-select-label">Disciplina</InputLabel>
                            <Select
                                labelId="disciplina-select-label" id="disciplina-select" value={disciplinaId} label="Disciplina"
                                onChange={(e) => setDisciplinaId(e.target.value)}
                                disabled={loadingDisciplinas || !!erroListagemDisciplinas}
                            >
                                <MenuItem value="" disabled>
                                    {loadingDisciplinas ? "Carregando..." : (erroListagemDisciplinas || "Selecione...")}
                                </MenuItem>
                                {!loadingDisciplinas && disciplinas.map((disciplina) => (
                                    <MenuItem key={disciplina.id} value={disciplina.id}>
                                        {disciplina.nome} ({disciplina.codigo})
                                    </MenuItem>
                                ))}
                            </Select>
                            {erroListagemDisciplinas && <Typography color="error" variant="caption" sx={{ml: 2}}>{erroListagemDisciplinas}</Typography>}
                        </FormControl>
                        <Button
                            type="submit" variant="contained" startIcon={<AddCircleOutlineIcon />}
                            disabled={loadingDisciplinas || !!erroListagemDisciplinas}
                            sx={{ mt: 1, alignSelf: 'flex-end' }}
                        >
                            Criar Turma
                        </Button>
                        {mensagemCriacao.text && (
                            <Alert severity={mensagemCriacao.type || 'info'} sx={{ mt: 2 }}>
                                {mensagemCriacao.text}
                            </Alert>
                        )}
                    </Box>
                </Paper>
            )}
        </Container>
    );
}

export default ProfessorDashboardPage;