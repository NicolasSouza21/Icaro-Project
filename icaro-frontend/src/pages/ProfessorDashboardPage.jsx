import { useState, useEffect } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

import { Alert, Box, Button, CircularProgress, Container, FormControl, InputLabel, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, IconButton, Tooltip } from '@mui/material'; // ✨ ALTERAÇÃO AQUI: Adiciona IconButton e Tooltip
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ListAltIcon from '@mui/icons-material/ListAlt';
import CreateIcon from '@mui/icons-material/Create';
// ✨ ALTERAÇÃO AQUI: Ícone para Abrir Chamada
import PlayCircleOutlineIcon from '@mui/icons-material/PlayCircleOutline';
// ✨ ALTERAÇÃO AQUI: Ícone para Chamada Já Aberta (Opcional)
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';


function ProfessorDashboardPage() {
    // Estado de visualização
    const [viewMode, setViewMode] = useState('list');

    // Estados do formulário
    const [nomeTurma, setNomeTurma] = useState('');
    const [semestre, setSemestre] = useState('2025.2');
    const [disciplinaId, setDisciplinaId] = useState('');
    const [mensagemCriacao, setMensagemCriacao] = useState({ type: '', text: '' });

    // Estados da lista de turmas
    const [turmas, setTurmas] = useState([]);
    const [loadingTurmas, setLoadingTurmas] = useState(false);
    const [erroListagemTurmas, setErroListagemTurmas] = useState('');

    // Estados da lista de disciplinas
    const [disciplinas, setDisciplinas] = useState([]);
    const [loadingDisciplinas, setLoadingDisciplinas] = useState(false);
    const [erroListagemDisciplinas, setErroListagemDisciplinas] = useState('');

    // --- ✨ ALTERAÇÃO AQUI: Estados para Aula Aberta ---
    const [aulaAbertaInfo, setAulaAbertaInfo] = useState(null); // Guarda dados da aula aberta (AulaResponseDTO)
    const [loadingAbrirAula, setLoadingAbrirAula] = useState(false); // Loading para o botão de abrir
    const [erroAbrirAula, setErroAbrirAula] = useState(''); // Erro ao abrir aula

    const { usuario } = useAuth();

    // --- Funções de Busca ---
    const buscarMinhasTurmas = async () => { /* ... como antes ... */
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
    const buscarDisciplinas = async () => { /* ... como antes ... */
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
        // TODO: Poderíamos buscar aqui se já existe alguma aula aberta para este professor
    }, []);

    // --- Lógica de Criação ---
    const handleCriarTurma = async (e) => { /* ... como antes ... */
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

     // --- ✨ ALTERAÇÃO AQUI: Lógica para Abrir Chamada ---
     const handleAbrirChamada = async (turmaIdParaAbrir) => {
        setLoadingAbrirAula(true);
        setErroAbrirAula('');
        setAulaAbertaInfo(null); // Limpa info anterior

        const requestData = { turmaId: turmaIdParaAbrir };

        try {
            // Chama o endpoint POST /api/v1/aulas
            const response = await api.post('/api/v1/aulas', requestData);
            setAulaAbertaInfo(response.data); // Guarda os dados da aula aberta (AulaResponseDTO)
            // Poderia mostrar uma mensagem de sucesso aqui se quisesse
        } catch (error) {
            console.error('Erro ao abrir aula:', error.response?.data || error.message);
            const errorMsg = error.response?.data?.message || 'Falha ao tentar abrir a chamada.';
            setErroAbrirAula(`Erro para Turma ID ${turmaIdParaAbrir}: ${errorMsg}`);
            // Limpa a info de aula aberta se der erro
            setAulaAbertaInfo(null);
        } finally {
            setLoadingAbrirAula(false);
        }
     };

    // --- Renderização ---
    return (
        <Container sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Dashboard do Professor
            </Typography>
            <Typography variant="subtitle1" gutterBottom sx={{ mb: 3 }}>
                Bem-vindo, {usuario?.email}!
            </Typography>

            {/* --- ✨ ALTERAÇÃO AQUI: Exibe info da Aula Aberta --- */}
            {aulaAbertaInfo && (
                <Alert severity="success" sx={{ mb: 2 }}>
                    Chamada ABERTA para Turma: {aulaAbertaInfo.nomeTurma} ({aulaAbertaInfo.nomeDisciplina}) - Aula ID: {aulaAbertaInfo.id} iniciada em {new Date(aulaAbertaInfo.dataHoraAula).toLocaleString()}
                    {/* TODO: Adicionar botão para "Fechar Chamada" aqui */}
                </Alert>
            )}
            {/* Exibe erro ao abrir aula, se houver */}
            {erroAbrirAula && (
                 <Alert severity="error" sx={{ mb: 2 }}>{erroAbrirAula}</Alert>
            )}


            {/* --- Visualização da Lista --- */}
            {viewMode === 'list' && (
                <Paper elevation={3} sx={{ p: 3 }}>
                     <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <ListAltIcon />
                            <Typography variant="h6">Minhas Turmas</Typography>
                        </Box>
                        <Button
                            variant="contained" startIcon={<AddCircleOutlineIcon />}
                            onClick={() => { setViewMode('create'); setMensagemCriacao({ type: '', text: '' }); }}
                        >
                            Criar Nova Turma
                        </Button>
                    </Box>

                    {loadingTurmas && <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}><CircularProgress /></Box>}
                    {erroListagemTurmas && <Alert severity="error" sx={{ mt: 2 }}>{erroListagemTurmas}</Alert>}

                    {!loadingTurmas && !erroListagemTurmas && (
                        <TableContainer component={Paper} elevation={1} sx={{ mt: 2 }}>
                            <Table sx={{ minWidth: 700 }} aria-label="tabela de turmas"> {/* Aumenta minWidth */}
                                <TableHead sx={{ backgroundColor: 'action.hover' }}>
                                    <TableRow>
                                        <TableCell sx={{ fontWeight: 'bold' }}>ID</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Nome Turma</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Semestre</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Disciplina</TableCell>
                                        {/* ✨ ALTERAÇÃO AQUI: Nova coluna de Ações */}
                                        <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Ações</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {turmas.length === 0 ? (
                                        <TableRow>
                                            <TableCell colSpan={5} align="center" sx={{ p: 4, fontStyle: 'italic' }}>Nenhuma turma encontrada.</TableCell>
                                        </TableRow>
                                    ) : (
                                        turmas.map((turma) => {
                                            // Verifica se esta turma é a que tem a aula aberta
                                            const isAulaAbertaParaEstaTurma = aulaAbertaInfo?.turmaId === turma.id;

                                            return (
                                                <TableRow key={turma.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                                    <TableCell>{turma.id}</TableCell>
                                                    <TableCell>{turma.nomeTurma}</TableCell>
                                                    <TableCell>{turma.semestre}</TableCell>
                                                    <TableCell>{turma.nomeDisciplina} ({turma.codigoDisciplina})</TableCell>
                                                    {/* ✨ ALTERAÇÃO AQUI: Célula com o botão */}
                                                    <TableCell align="center">
                                                        {isAulaAbertaParaEstaTurma ? (
                                                            // Se a aula já está aberta para ESTA turma, mostra um status
                                                             <Tooltip title={`Chamada já aberta (Aula ID: ${aulaAbertaInfo.id})`}>
                                                                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'success.main' }}>
                                                                    <RadioButtonCheckedIcon fontSize="small" sx={{ mr: 0.5 }} />
                                                                    Aberta
                                                                </Box>
                                                            </Tooltip>
                                                        ) : (
                                                            // Botão para abrir chamada
                                                            <Tooltip title="Abrir Chamada para esta Turma">
                                                                {/* Span necessário para Tooltip em botão desabilitado */}
                                                                <span>
                                                                    <IconButton
                                                                        color="primary"
                                                                        onClick={() => handleAbrirChamada(turma.id)}
                                                                        // Desabilita se já houver ALGUMA aula aberta ou se estiver carregando
                                                                        disabled={loadingAbrirAula || !!aulaAbertaInfo}
                                                                        size="small"
                                                                    >
                                                                        <PlayCircleOutlineIcon />
                                                                    </IconButton>
                                                                </span>
                                                            </Tooltip>
                                                        )}
                                                        {/* TODO: Adicionar botão/ícone para Ver Alunos/Presenças */}
                                                    </TableCell>
                                                </TableRow>
                                            );
                                        })
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
                    { /* ... Conteúdo do Formulário (inalterado) ... */}
                    {/* ... */}
                 </Paper>
            )}
        </Container>
    );
}

export default ProfessorDashboardPage;