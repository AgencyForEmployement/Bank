package com.agency.bank.service;

import com.agency.bank.dto.CardDto;
import com.agency.bank.dto.PaymentForBankRequestDto;
import com.agency.bank.dto.PaymentResponseDTO;
import com.agency.bank.enums.TransactionStatus;
import com.agency.bank.model.Card;
import com.agency.bank.model.Client;
import com.agency.bank.model.Reservation;
import com.agency.bank.model.Transaction;
import com.agency.bank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private CardService cardService;
    private ReservationService reservationService;
    private ClientService clientService;

    @Value("${app.paymentUrl}")
    private String paymentUrl;

    @Value("${app.panAcquirer}")
    private String panAcquirer;

    public Transaction pay(CardDto cardDto) {
        Transaction transaction = transactionRepository.findByPaymentId(cardDto.getPaymentId());
        Client client = clientService.findByPan(cardDto.getPan()); //kupac
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
                        .amount(cardDto.getAmount())
                        .client(client)
                        .acquirerAccountNumber(acquirer.getAccount().getAccountNumber())
                        .build();
                transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
                transaction.setAcquirerOrderId(generateRandomNumber());
                transaction.setAcquirerTimestamp(LocalDateTime.now());
                reservationService.save(reservation);
            } else
                transaction.setTransactionStatus(TransactionStatus.FAILED); //klijent nema dovoljno raspolozivih sredstava pa je transakicja neuspesna
        } else
            return null; // na kontroleru ce ovim da se preusmeri na korak 3b,4,5,6 -----------> slucaj kada su razlicite banke

        transactionRepository.save(transaction);
        return transaction;
    }

    private boolean checkClientAccountState(double amount, Client client) {
        double sum = 0;
        if (client.getReservations().size() > 0)
          sum = sumReservations(client.getReservations());

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
        if (pan.substring(0,6).equals(panAcquirer))
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
        Transaction transaction = Transaction.builder()
                                                .paymentId(generateRandomNumber())
                                                .transactionStatus(TransactionStatus.PAYMENT_REQUESTED)
                                                .merchantOrderId(paymentForBankRequestDto.getMerchantOrderId())
                                                .merchantTimestamp(paymentForBankRequestDto.getMerchantTimestamp())
                                                .amount(paymentForBankRequestDto.getAmount())
                                                .build();

        transactionRepository.saveAndFlush(transaction);

        return PaymentResponseDTO.builder()
                .paymentId(transaction.getPaymentId())
                .paymentURL(paymentUrl)
                .build();
    }

    private int generateRandomNumber() {
        int m = (int) Math.pow(10, 10 - 1);
        return m + new Random().nextInt(9 * m);
    }

    public String getPaymentURL(Transaction transaction, CardDto cardDto) {
        if (transaction.getTransactionStatus() == TransactionStatus.SUCCESS || transaction.getTransactionStatus() == TransactionStatus.IN_PROGRESS)
            return cardDto.getSuccessUrl().getURL();
        else if (transaction.getTransactionStatus() == TransactionStatus.FAILED)
            return cardDto.getFailedUrl().getURL();
        else
            return cardDto.getErrorUrl().getURL();
    }

    @Scheduled(cron = "${greeting.cron}")
    private void finishTransactions(){
        //nisam ova cuvanja verovatno napisala kako treba zato je zakomentarisano
       // Client acquirer = clientService.findByPan(panAcquirer);//prodji kroz sve klijente i sve njihove rezervacije
//        for (Reservation reservation: reservationService.getAllWithClients()) {
//            double newAmount = reservation.getClient().getAccount().getAmount() - reservation.getAmount();//smanji novac kupcu
//            reservation.getClient().getAccount().setAmount(newAmount);
//            reservationService.save(reservation);
//            double newAmountForAcquirer = acquirer.getAccount().getAmount() + reservation.getAmount(); //povecaj novac prodavcu
//            acquirer.getAccount().setAmount(newAmountForAcquirer);
//            reservationService.deleteById(reservation);//izbrsi rezervaciju
//        }
        //nije promenjena transakcija u success
    }
}
