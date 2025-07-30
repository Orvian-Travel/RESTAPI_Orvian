package com.orvian.travelapi.controller.impl;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.controller.dto.payment.UpdatePaymentDTO;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.service.exception.AccessDeniedException;
import com.orvian.travelapi.service.impl.PaymentServiceImpl;
import com.orvian.travelapi.service.security.OrvianAuthorizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Pagamento", description = "Operações relacionadas ao gerenciamento de pagamentos")
public class PaymentControllerImpl implements GenericController {

    private final PaymentServiceImpl paymentService;

    private final OrvianAuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Processar Pagamento", description = "Processa um pagamento com os dados fornecidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "409", description = "Pagamento com ID repetido", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> createPayment(@RequestBody @Valid CreatePaymentDTO dto) {
        if (!authorizationService.canModifyResource("CREATE", "payment")) {
            throw new AccessDeniedException("Apenas administradores podem processar pagamentos");
        }

        Payment payment = paymentService.create(dto);

        URI location = generateHeaderLocation(payment.getId()); // Gera a URI do novo pagamento
        return ResponseEntity.created(location).build(); // Retorna 201 (Created) com a URI no cabeçalho Location
    }

    @GetMapping
    @Operation(summary = "Buscar todos os pagamentos", description = "Recupera a lista de todos os pagamentos registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamentos recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<List<PaymentSearchResultDTO>> getAllPayments() {

        if (!authorizationService.isCurrentUserAttendenteOrAdmin()) {
            throw new AccessDeniedException("Apenas administradores e atendentes podem listar pagamentos");
        }

        log.info("Fetching all payments");
        List<PaymentSearchResultDTO> payments = paymentService.findAll();
        log.info("Total payments found: {}", payments.size());
        return ResponseEntity.ok(payments); // Retorna a lista de pagamentos com status 200 (OK)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um pagamento pelo ID", description = "Recupera um pagamento identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PaymentSearchResultDTO> getPaymentById(@PathVariable UUID id) {
        if (!authorizationService.isCurrentUserAttendenteOrAdmin()) {
            throw new AccessDeniedException("Apenas administradores e atendentes podem buscar pagamentos");
        }

        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um pagamento existente", description = "Atualiza os dados de um pagamento existente, identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pagamento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> updatePayment(@PathVariable UUID id, @RequestBody @Valid UpdatePaymentDTO dto) {
        if (!authorizationService.canModifyResource("UPDATE", "payment")) {
            throw new AccessDeniedException("Apenas administradores podem atualizar pagamentos");
        }

        log.info("Updating payment with id: {}", id);
        paymentService.update(id, dto);
        return ResponseEntity.noContent().build(); // Retorna 204 (No Content) se a atualização for bem-sucedida
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um pagamento", description = "Exclui um pagamento identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pagamento excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {

        if (!authorizationService.canModifyResource("DELETE", "payment")) {
            throw new AccessDeniedException("Apenas administradores podem excluir pagamentos");
        }

        log.info("Deleting payment with id: {}", id);
        paymentService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 (No Content) se a exclusão for bem-sucedida
    }

}
