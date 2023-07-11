package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class TmFileCheckDto {
    private boolean allCorrect;
    // Header Record
    private FieldCheckDto recordType = new FieldCheckDto();
    private FieldCheckDto fileName = new FieldCheckDto();
    private FieldCheckDto bankBinIso = new FieldCheckDto();
    private FieldCheckDto headerRecordEndCharacter = new FieldCheckDto();

    // Second record
    private FieldCheckDto recordType2 = new FieldCheckDto();
    private FieldCheckDto terminalSequentialNumber = new FieldCheckDto();
    private FieldCheckDto terminalUpdateCode = new FieldCheckDto();
    private FieldCheckDto merchantContractNumber = new FieldCheckDto();
    private FieldCheckDto terminalId = new FieldCheckDto();
    private FieldCheckDto terminalLabel = new FieldCheckDto();
    private FieldCheckDto addressLine1 = new FieldCheckDto();
    private FieldCheckDto addressLine2 = new FieldCheckDto();
    private FieldCheckDto addressLine3 = new FieldCheckDto();
    private FieldCheckDto addressLine4 = new FieldCheckDto();
    private FieldCheckDto addressLine5 = new FieldCheckDto();
    private FieldCheckDto addressLine6 = new FieldCheckDto();
    private FieldCheckDto addressLine7 = new FieldCheckDto();
    private FieldCheckDto addressLine8 = new FieldCheckDto();
    private FieldCheckDto terminalPhoneNumber = new FieldCheckDto();
    private FieldCheckDto terminalFaxNumber = new FieldCheckDto();
    private FieldCheckDto terminalMobileNumber = new FieldCheckDto();
    private FieldCheckDto terminalEmailAddress = new FieldCheckDto();
    private FieldCheckDto trxDebit = new FieldCheckDto();
    private FieldCheckDto trxRemb = new FieldCheckDto();
    private FieldCheckDto trxAnnul = new FieldCheckDto();
    private FieldCheckDto trxSolde = new FieldCheckDto();
    private FieldCheckDto trxPAutor = new FieldCheckDto();
    private FieldCheckDto trxTel = new FieldCheckDto();
    private FieldCheckDto trxRetrait = new FieldCheckDto();
    private FieldCheckDto trxCashAdvance = new FieldCheckDto();
    private FieldCheckDto trxPaiementFacture = new FieldCheckDto();
    private FieldCheckDto hourStart = new FieldCheckDto();
    private FieldCheckDto hourEnd = new FieldCheckDto();
    private FieldCheckDto terminalCardTypeUpdate = new FieldCheckDto();
    private FieldCheckDto cardType0 = new FieldCheckDto();
    private FieldCheckDto limit0 = new FieldCheckDto();
    private FieldCheckDto cardType1 = new FieldCheckDto();
    private FieldCheckDto limit1 = new FieldCheckDto();
    private FieldCheckDto cardType2 = new FieldCheckDto();
    private FieldCheckDto limit2 = new FieldCheckDto();
    private FieldCheckDto cardType3 = new FieldCheckDto();
    private FieldCheckDto limit3 = new FieldCheckDto();
    private FieldCheckDto cardType4 = new FieldCheckDto();
    private FieldCheckDto limit4 = new FieldCheckDto();
    private FieldCheckDto cardType5 = new FieldCheckDto();
    private FieldCheckDto limit5 = new FieldCheckDto();
    private FieldCheckDto cardType6 = new FieldCheckDto();
    private FieldCheckDto limit6 = new FieldCheckDto();
    private FieldCheckDto terminalType = new FieldCheckDto();
    private FieldCheckDto secondRecordEndCharacter = new FieldCheckDto();

    // End Record
    private FieldCheckDto recordType3 = new FieldCheckDto();
    private FieldCheckDto recordsNumber = new FieldCheckDto();
    private FieldCheckDto lastRecordEndCharacter = new FieldCheckDto();


}
