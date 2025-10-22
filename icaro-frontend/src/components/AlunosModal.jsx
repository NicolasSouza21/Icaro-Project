// src/components/AlunosModal.jsx

import { useState, useEffect, useCallback } from 'react';
import api from '../services/api';
import {
    Alert, Box, Button, CircularProgress, Dialog, DialogTitle, DialogContent,
    DialogActions, List, ListItem, ListItemText, ListItemAvatar, Avatar, IconButton, Tooltip,
    Divider, TextField, Typography // ✨ ALTERAÇÃO AQUI: Typography foi adicionado
} from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import CloseIcon from '@mui/icons-material/Close';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PersonAddIcon from '@mui/icons-material/PersonAdd';

function AlunosModal({ open, onClose, turma, aulaAbertaInfo }) {
    const [alunos, setAlunos] = useState([]);
    const [loadingAlunos, setLoadingAlunos] = useState(false);
    const [erroAlunos, setErroAlunos] = useState('');

    const [presencasRegistradas, setPresencasRegistradas] = useState([]);
    const [loadingPresenca, setLoadingPresenca] = useState(null);
    const [erroPresenca, setErroPresenca] = useState('');

    const [alunoIdParaMatricular, setAlunoIdParaMatricular] = useState('');
    const [loadingMatricula, setLoadingMatricula] = useState(false);
    const [erroMatricula, setErroMatricula] = useState('');
    const [sucessoMatricula, setSucessoMatricula] = useState('');

    const fetchAlunos = useCallback(async () => {
        if (!turma) return;

        setLoadingAlunos(true);
        setErroAlunos('');
        setErroPresenca('');
        setErroMatricula('');
        setSucessoMatricula('');

        try {
            const response = await api.get(`/api/v1/turmas/${turma.id}/alunos`);
            setAlunos(response.data);
        } catch (error) {
            console.error('Erro ao buscar alunos:', error.response?.data || error.message);
            setErroAlunos('Não foi possível carregar a lista de alunos.');
        } finally {
            setLoadingAlunos(false);
        }
    }, [turma]);

    useEffect(() => {
        if (open) {
            fetchAlunos();
            setPresencasRegistradas([]);
        }
    }, [open, fetchAlunos]);

    const handleRegistrarPresenca = async (alunoId) => {
        setLoadingPresenca(alunoId);
        setErroPresenca('');
        try {
            await api.post(`/api/v1/aulas/${aulaAbertaInfo.id}/presencas`, { alunoId });
            setPresencasRegistradas(prev => [...prev, alunoId]);
        } catch (error) {
            console.error('Erro ao registrar presença:', error.response?.data || error.message);
            const errorMsg = error.response?.data?.message || 'Falha ao registrar presença.';
            setErroPresenca(`Erro para o aluno ID ${alunoId}: ${errorMsg}`);
        } finally {
            setLoadingPresenca(null);
        }
    };

    const handleMatricularAluno = async (e) => {
        e.preventDefault();
        setLoadingMatricula(true);
        setErroMatricula('');
        setSucessoMatricula('');

        const requestBody = {
            turmaId: turma.id,
            alunoId: Number(alunoIdParaMatricular)
        };

        try {
            await api.post('/api/v1/matriculas', requestBody);
            setSucessoMatricula(`Aluno (ID: ${alunoIdParaMatricular}) matriculado com sucesso!`);
            setAlunoIdParaMatricular('');
            await fetchAlunos();
        } catch (error) {
            console.error('Erro ao matricular aluno:', error.response?.data || error.message);
            const errorMsg = error.response?.data?.message || 'Falha ao matricular aluno.';
            setErroMatricula(errorMsg);
        } finally {
            setLoadingMatricula(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                Alunos da Turma: {turma?.nomeTurma}
                <IconButton onClick={onClose}><CloseIcon /></IconButton>
            </DialogTitle>
            <DialogContent dividers>
                {erroPresenca && <Alert severity="error" sx={{ mb: 2 }}>{erroPresenca}</Alert>}

                {loadingAlunos && <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}><CircularProgress /></Box>}
                {erroAlunos && <Alert severity="error">{erroAlunos}</Alert>}
                {!loadingAlunos && !erroAlunos && (
                    <List>
                        {alunos.length === 0 ? (
                            <ListItem><ListItemText primary="Nenhum aluno matriculado nesta turma." /></ListItem>
                        ) : (
                            alunos.map(aluno => {
                                const alunoPresente = presencasRegistradas.includes(aluno.id);
                                return (
                                    <ListItem key={aluno.id} secondaryAction={
                                        alunoPresente ? (
                                            <Box sx={{ display: 'flex', alignItems: 'center', color: 'success.main' }}>
                                                <CheckCircleIcon sx={{ mr: 1 }} />
                                                Presente
                                            </Box>
                                        ) : (
                                        <Tooltip title="Registrar Presença Manual">
                                            <span>
                                                <Button
                                                    variant="outlined" size="small"
                                                    onClick={() => handleRegistrarPresenca(aluno.id)}
                                                    disabled={!aulaAbertaInfo || aulaAbertaInfo.turmaId !== turma?.id || loadingPresenca === aluno.id}
                                                >
                                                    {loadingPresenca === aluno.id ? <CircularProgress size={20} /> : 'Presente'}
                                                </Button>
                                            </span>
                                        </Tooltip>
                                        )
                                    }>
                                        <ListItemAvatar><Avatar><PersonIcon /></Avatar></ListItemAvatar>
                                        <ListItemText primary={aluno.nome} secondary={`RA: ${aluno.matriculaRa} - Email: ${aluno.email}`} />
                                    </ListItem>
                                );
                            })
                        )}
                    </List>
                )}
                <Divider sx={{ my: 2 }} />
                
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Fechar</Button>
            </DialogActions>
        </Dialog>
    );
}

export default AlunosModal;