#!/bin/bash

for cpr in {"customer1-17","customer2-17","merchant1-17","merchant2-17","Alice-17","Bob-17","Charlie-17","Dave-17"}
do
    response="$(curl --location 'http://fm-00.compute.dtu.dk/BankService' \
    --header 'SOAPAction;' \
    --header 'Content-Type: application/xml' \
    --data '<?xml version="1.0"?>

    <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
        <S:Body>
            <ns1:getAccountByCprNumber xmlns:ns1="http://fastmoney.ws.dtu/">
                <cpr>'"$cpr"'</cpr>
            </ns1:getAccountByCprNumber>
        </S:Body>
    </S:Envelope>')"

    accountId="$(echo "$response" | grep -oP "(?<=<id>)(.*)(?=</id>)")"

    curl --location 'http://fm-00.compute.dtu.dk/BankService' \
    --header 'SOAPAction;' \
    --header 'Content-Type: application/xml' \
    --data '<?xml version="1.0"?>

    <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
        <S:Body>
            <ns1:retireAccount xmlns:ns1="http://fastmoney.ws.dtu/">
                <account_id>'"$accountId"'</account_id>
            </ns1:retireAccount>
        </S:Body>
    </S:Envelope>'
done