import { useState, useEffect } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

// ✨ ALTERAÇÃO AQUI: Importa o novo componente do modal
import AlunosModal from '../components/AlunosModal';

// ✨ ALTERAÇÃO AQUI: Imports do Material-UI foram simplificados
import {
    Alert, Box, Button, CircularProgress, Container, FormControl, InputLabel, MenuItem, Paper, Select,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, IconButton, Tooltip
} from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ListAltIcon from '@mui/icons-material/ListAlt';
import CreateIcon from '@mui/icons-material/Create';
import PlayCircleOutlineIcon from '@mui/icons-material/PlayCircleOutline';
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import GroupIcon from '@mui/icons-material/Group';


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

    // Estados para Aula Aberta
    const [aulaAbertaInfo, setAulaAbertaInfo] = useState(null);
    const [loadingAbrirAula, setLoadingAbrirAula] = useState(false);
    const [erroAbrirAula, setErroAbrirAula] = useState('');

    // --- ✨ ALTERAÇÃO AQUI: Estados para controlar o Modal foram simplificados ---
    const [modalAberto, setModalAberto] = useState(false);
    const [turmaSelecionada, setTurmaSelecionada] = useState(null); // Apenas para saber qual turma passar para o modal
    // --- FIM DA ALTERAÇÃO ---

    const { usuario } = useAuth();

    // --- Funções de Busca (inalteradas) ---
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
    }, []);

    // --- Lógica de Criação (inalterada) ---
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

     // --- Lógica para Abrir Chamada (inalterada) ---
     const handleAbrirChamada = async (turmaIdParaAbrir) => { /* ... como antes ... */
        setLoadingAbrirAula(true);
        setErroAbrirAula('');
        setAulaAbertaInfo(null);
        const requestData = { turmaId: turmaIdParaAbrir };
        try {
            const response = await api.post('/api/v1/aulas', requestData);
            setAulaAbertaInfo(response.data);
        } catch (error) {
            console.error('Erro ao abrir aula:', error.response?.data || error.message);
            const errorMsg = error.response?.data?.message || 'Falha ao tentar abrir a chamada.';
            setErroAbrirAula(`Erro para Turma ID ${turmaIdParaAbrir}: ${errorMsg}`);
            setAulaAbertaInfo(null);
        } finally {
            setLoadingAbrirAula(false);
        }
     };

    // --- ✨ ALTERAÇÃO AQUI: Funções de controle do modal simplificadas ---
    const handleAbrirModalAlunos = (turma) => {
        setTurmaSelecionada(turma);
        setModalAberto(true);
    };

    const handleFecharModal = () => {
        setModalAberto(false);
        setTurmaSelecionada(null);
    };
    // --- FIM DA ALTERAÇÃO ---

    // --- Renderização ---
    return (
        <Container sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom> Dashboard do Professor </Typography>
            <Typography variant="subtitle1" gutterBottom sx={{ mb: 3 }}> Bem-vindo, {usuario?.email}! </Typography>

            {aulaAbertaInfo && ( <Alert severity="success" sx={{ mb: 2 }}> Chamada ABERTA para Turma: {aulaAbertaInfo.nomeTurma} ({aulaAbertaInfo.nomeDisciplina}) - Aula ID: {aulaAbertaInfo.id} iniciada em {new Date(aulaAbertaInfo.dataHoraAula).toLocaleString()} </Alert> )}
            {erroAbrirAula && ( <Alert severity="error" sx={{ mb: 2 }}>{erroAbrirAula}</Alert> )}


            {viewMode === 'list' && (
                <Paper elevation={3} sx={{ p: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}> <ListAltIcon /> <Typography variant="h6">Minhas Turmas</Typography> </Box>
                        <Button variant="contained" startIcon={<AddCircleOutlineIcon />} onClick={() => { setViewMode('create'); setMensagemCriacao({ type: '', text: '' }); }} > Criar Nova Turma </Button>
                    </Box>

                    {loadingTurmas && <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}><CircularProgress /></Box>}
                    {erroListagemTurmas && <Alert severity="error" sx={{ mt: 2 }}>{erroListagemTurmas}</Alert>}

                    {!loadingTurmas && !erroListagemTurmas && (
                        <TableContainer component={Paper} elevation={1} sx={{ mt: 2 }}>
                            <Table sx={{ minWidth: 700 }} aria-label="tabela de turmas">
                                <TableHead sx={{ backgroundColor: 'action.hover' }}>
                                    <TableRow>
                                        <TableCell sx={{ fontWeight: 'bold' }}>ID</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Nome Turma</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Semestre</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>Disciplina</TableCell>
                                        <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Ações</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {turmas.length === 0 ? (
                                        <TableRow> <TableCell colSpan={5} align="center" sx={{ p: 4, fontStyle: 'italic' }}>Nenhuma turma encontrada.</TableCell> </TableRow>
                                    ) : (
                                        turmas.map((turma) => {
                                            const isAulaAbertaParaEstaTurma = aulaAbertaInfo?.turmaId === turma.id;
                                            return (
                                                <TableRow key={turma.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                                    <TableCell>{turma.id}</TableCell>
                                                    <TableCell>{turma.nomeTurma}</TableCell>
                                                    <TableCell>{turma.semestre}</TableCell>
                                                    <TableCell>{turma.nomeDisciplina} ({turma.codigoDisciplina})</TableCell>
                                                    <TableCell align="center">
                                                        <Tooltip title="Ver Alunos da Turma">
                                                            <IconButton color="default" onClick={() => handleAbrirModalAlunos(turma)} size="small">
                                                                <GroupIcon />
                                                            </IconButton>
                                                        </Tooltip>
                                                        {isAulaAbertaParaEstaTurma ? (
                                                            <Tooltip title={`Chamada já aberta (Aula ID: ${aulaAbertaInfo.id})`}>
                                                                <Box component="span" sx={{ display: 'inline-flex', alignItems: 'center', color: 'success.main', ml: 1 }}>
                                                                    <RadioButtonCheckedIcon fontSize="small" />
                                                                </Box>
                                                            </Tooltip>
                                                        ) : (
                                                            <Tooltip title="Abrir Chamada para esta Turma">
                                                                <span>
                                                                    <IconButton color="primary" onClick={() => handleAbrirChamada(turma.id)} disabled={loadingAbrirAula || !!aulaAbertaInfo} size="small" sx={{ ml: 1 }}>
                                                                        <PlayCircleOutlineIcon />
                                                                    </IconButton>
                                                                </span>
                                                            </Tooltip>
                                                        )}
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

            {viewMode === 'create' && ( <Paper elevation={3} sx={{ p: 3 }}> { /* Formulário... */ } </Paper> )}

            {/* --- ✨ ALTERAÇÃO AQUI: Renderiza o componente do Modal --- */}
            <AlunosModal
                open={modalAberto}
                onClose={handleFecharModal}
                turma={turmaSelecionada}
                aulaAbertaInfo={aulaAbertaInfo}
            />
            {/* --- FIM DA ALTERAÇÃO --- */}
        </Container>
    );
}

export default ProfessorDashboardPage;