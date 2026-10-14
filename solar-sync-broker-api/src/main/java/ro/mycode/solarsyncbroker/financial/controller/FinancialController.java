package ro.mycode.solarsyncbroker.financial.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mycode.solarsyncbroker.financial.dtos.FinancialtransactionResponseToDto;
import ro.mycode.solarsyncbroker.financial.mapper.FinancialMapper;
import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;
import ro.mycode.solarsyncbroker.financial.service.queryService.FinancialQueryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/houses")
public class FinancialController {

    private final FinancialQueryService financialQueryService;

    public FinancialController(FinancialQueryService financialQueryService) {
        this.financialQueryService = financialQueryService;
    }

    @GetMapping("/{id}/financial-summary")
    @PreAuthorize("hasAuthority('FINANCIAL_VIEW')")
    public ResponseEntity<List<FinancialtransactionResponseToDto>> getHouseFinancialSummary(@PathVariable Long id) {
        List<FinancialTransaction> transactions = financialQueryService.getTransactionsByHouseId(id);

        List<FinancialtransactionResponseToDto> dtos = transactions.stream()
                .map(FinancialMapper::financialTransactionToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/community/financial-summary")
    @PreAuthorize("hasAuthority('COMMUNITY_VIEW')")
    public ResponseEntity<List<FinancialtransactionResponseToDto>> getCommunityFinancialSummary() {
        List<FinancialTransaction> allTransactions = financialQueryService.getAllTransactions();

        List<FinancialtransactionResponseToDto> dtos = allTransactions.stream()
                .map(FinancialMapper::financialTransactionToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}