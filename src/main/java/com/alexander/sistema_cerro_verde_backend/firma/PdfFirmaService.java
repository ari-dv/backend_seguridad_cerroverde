package com.alexander.sistema_cerro_verde_backend.firma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

@Service
public class PdfFirmaService {

    @Value("${firma.cert.path}")
    private String certPath;

    @Value("${firma.cert.password}")
    private String certPassword;

    public byte[] firmarPdf(byte[] pdfBytes, String nombreFirmante) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        KeyStore keyStore = generarCertificadoAutofirmado(nombreFirmante, "12345678", certPassword.toCharArray());

        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey
                = (PrivateKey) keyStore.getKey(alias, certPassword.toCharArray());
        Certificate[] chain = keyStore.getCertificateChain(alias);

        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfSigner signer = new PdfSigner(
                reader,
                baos,
                new StampingProperties().useAppendMode()
        );

        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Firma automática del sistema")
                .setLocation("Cerro Verde")
                .setLayer2Text(
                        "Firmado por:\n"
                        + nombreFirmante + "\n"
                        + "Fecha: " + LocalDate.now()
                );

        ImageData logo = ImageDataFactory.create(
                new ClassPathResource("static/img/logo-cerroverde2.png")
                        .getInputStream()
                        .readAllBytes()
        );

        appearance.setSignatureGraphic(logo);
        appearance.setRenderingMode(
                PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION
        );

// 🔥 POSICIÓN ARRIBA DERECHA
        var pageSize = signer.getDocument()
                .getPage(1)
                .getPageSize();

        float firmaAncho = 90;
        float firmaAlto = 45;

        float x = pageSize.getWidth() - firmaAncho - 36;
        float y = pageSize.getHeight() - firmaAlto - 24;

        appearance.setPageRect(new Rectangle(x, y, firmaAncho, firmaAlto));
        appearance.setPageNumber(1);

        signer.setFieldName("firma_cerroverde");

        IExternalSignature signature = new PrivateKeySignature(
                privateKey,
                DigestAlgorithms.SHA256,
                BouncyCastleProvider.PROVIDER_NAME
        );

        signer.signDetached(
                new BouncyCastleDigest(),
                signature,
                chain,
                null,
                null,
                null,
                0,
                PdfSigner.CryptoStandard.CADES
        );

        return baos.toByteArray();
    }

    private KeyStore generarCertificadoAutofirmado(String nombreFirmante, String dni, char[] password) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        X500Name subject = new X500Name(
                "CN=" + nombreFirmante
                + ", OU=DNI " + dni
                + ", O=Proyecto Curso"
                + ", C=PE"
        );

        Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60);
        Date notAfter = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1 año
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                subject,
                serial,
                notBefore,
                notAfter,
                subject,
                keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer));

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, null);
        ks.setKeyEntry("firma", keyPair.getPrivate(), password, new Certificate[]{cert});

        return ks;
    }

}
