package com.agency.bank.service;

import com.agency.bank.dto.CardDto;
import com.agency.bank.dto.PaymentForBankRequestDto;
import com.agency.bank.dto.PaymentResponseDTO;
import com.agency.bank.enums.TransactionStatus;
import com.agency.bank.model.*;
import com.agency.bank.repository.AccountRepository;
import com.agency.bank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@PropertySource(value = "application.properties", ignoreResourceNotFound = true)
@AllArgsConstructor
@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private CardService cardService;
    private ReservationService reservationService;
    private AccountRepository accountRepository;
    private ClientService clientService;
    private static String paymentUrl;
    private static String panAcquirer;

    @Value("${bank.paymentUrl}")
    public void setPaymentUrl(String paymentUrl){
        this.paymentUrl = paymentUrl;
    }

    @Value("${bank.panAcquirer}")
    public void setPanAcquirer(String panAcquirer){
        this.panAcquirer = panAcquirer;
    }

    public Transaction pay(CardDto cardDto) {
        Client client = clientService.findByPan(cardDto.getPan()); //kupac
        Transaction transaction = transactionRepository.findByPaymentId(Integer.parseInt(cardDto.getPaymentId()), client.getId());
        Client acquirer = clientService.findByPan(panAcquirer); //prodavac

        //provarava validnost dobijenih podataka
        if (!checkValidityOfIssuerCardData(cardDto)){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            return transaction;
        }

        //provera da li je ista banka
        if (sameBankForAcquirerAndIssuer(cardDto.getPan())){
            //provera raspolozivih sredstava
            if (checkClientAccountState(transaction.getAmount(), client)){
                //rezervacija sredstava
                Reservation reservation = Reservation.builder()
                        .description(cardDto.getDescription())
                        .amount(Double.parseDouble(cardDto.getAmount()))
                        .acquirerAccountNumber(acquirer.getAccount().getAccountNumber())
                        .client(client)
                        .build();
                transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
                transaction.setClient(client);
                reservationService.save(reservation);
            } else {
                transaction.setTransactionStatus(TransactionStatus.FAILED); //klijent nema dovoljno raspolozivih sredstava pa je transakicja neuspesna
            }
        } else {
            return null; // na kontroleru ce ovim da se preusmeri na korak 3b,4,5,6 -----------> slucaj kada su razlicite banke
        }
        transactionRepository.save(transaction);
        createTransactionForAcquirer(transaction, acquirer);
        return transaction;
    }

    private void createTransactionForAcquirer(Transaction transaction, Client acquirer) {
        Transaction acquirerTransaction = Transaction.builder()
                .transactionStatus(transaction.getTransactionStatus())
                .paymentId(transaction.getPaymentId())
                .description(transaction.getDescription())
                .merchantTimestamp(transaction.getMerchantTimestamp())
                .merchantOrderId(transaction.getMerchantOrderId())
                .amount(transaction.getAmount())
                .client(acquirer)
                .build();
        transactionRepository.save(acquirerTransaction);
    }

    private boolean checkClientAccountState(double amount, Client client) {
        double sum = 0;
        if (client.getReservations().size() > 0) {
            sum = sumReservations(client.getReservations());
        }

        if ((client.getAccount().getAmount() - sum - amount) >= 0) //uzima u obzir i neobradjene rezervacije
            return true;
        else
            return false;
    }

    private double sumReservations(List<Reservation> reservations) { //sabira sumu svih rezervacija
        double sum = 0;
        for (Reservation reservation: reservations) {
            sum += reservation.getAmount();
        }
        return sum;
    }

    private boolean sameBankForAcquirerAndIssuer(String pan) {
        if (pan.substring(0,7).equals(panAcquirer.substring(0,7)))
            return true;
        return false;
    }

    private boolean checkValidityOfIssuerCardData(CardDto cardDto) {
        Card card = cardService.findByPan(cardDto.getPan());
        if (card == null || !card.getDateExpiration().equals(cardDto.getDateExpiration().trim()) || !card.getSecurityCode().equals(cardDto.getSecurityCode()))
            return false;
        return true;
    }

    public PaymentResponseDTO requestPayment(PaymentForBankRequestDto paymentForBankRequestDto) {
        //provera merchant info

        Transaction transaction = Transaction.builder()
                                                .paymentId(generateRandomNumber())
                                                .transactionStatus(TransactionStatus.PAYMENT_REQUESTED)
                                                .merchantOrderId(paymentForBankRequestDto.getMerchantOrderId())
                                                .merchantTimestamp(paymentForBankRequestDto.getMerchantTimestamp())
                                                .amount(paymentForBankRequestDto.getAmount())
                                                .description(paymentForBankRequestDto.getDescription())
                                                .build();

        transactionRepository.save(transaction);

        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .paymentId(transaction.getPaymentId())
                .paymentURL(paymentUrl)
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .successUrl(paymentForBankRequestDto.getSuccessUrl())
                .errorUrl(paymentForBankRequestDto.getErrorUrl())
                .failedUrl(paymentForBankRequestDto.getFailedUrl())
                .build();

        return response;
    }

    private int generateRandomNumber() {
        int m = (int) Math.pow(10, 10 - 1);
        return m + new Random().nextInt(9 * m);
    }

    public String getPaymentURL(Transaction transaction, CardDto cardDto) {
        if (transaction.getTransactionStatus() == TransactionStatus.SUCCESS || transaction.getTransactionStatus() == TransactionStatus.IN_PROGRESS)
            return cardDto.getSuccessUrl();
        else if (transaction.getTransactionStatus() == TransactionStatus.FAILED)
            return cardDto.getFailedUrl();
        else
            return cardDto.getErrorUrl();
    }

    @Scheduled(cron = "${greeting.cron}")
    private void finishTransactions(){
        //nisam ova cuvanja verovatno napisala kako treba zato je zakomentarisano
        Client acquirer = clientService.findByPan(panAcquirer);//prodji kroz sve klijente i sve njihove rezervacije
        for (Reservation reservation: reservationService.getAllWithClients()) {
            double newAmount = reservation.getClient().getAccount().getAmount() - reservation.getAmount();//smanji novac kupcu
            Account clientAccount = reservation.getClient().getAccount();
            clientAccount.setAmount(newAmount);
            accountRepository.save(clientAccount);
            double newAmountForAcquirer = acquirer.getAccount().getAmount() + reservation.getAmount(); //povecaj novac prodavcu
            Account acquirerAccount = acquirer.getAccount();
            acquirerAccount.setAmount(newAmountForAcquirer);
            accountRepository.save(acquirerAccount);
            reservation.setClient(null);
            reservationService.save(reservation);
            reservationService.delete(reservation);//izbrsi rezervaciju
        }
        //nije promenjena transakcija u success
    }
}
