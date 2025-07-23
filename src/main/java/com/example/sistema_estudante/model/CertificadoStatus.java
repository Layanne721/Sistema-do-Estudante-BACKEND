// Local: src/main/java/com/example/sistema_estudante/model/CertificadoStatus.java
package com.example.sistema_estudante.model;

public enum CertificadoStatus {
    PENDENTE,            // Aluno enviou, aguardando revisão do professor.
    APROVADO,            // Professor revisou e aprovou.
    REPROVADO,           // Professor revisou e reprovou (estado final de não aceitação).
    REVISAO_NECESSARIA   // Professor revisou, mas o certificado precisa de ajustes e reenvio pelo aluno.
}