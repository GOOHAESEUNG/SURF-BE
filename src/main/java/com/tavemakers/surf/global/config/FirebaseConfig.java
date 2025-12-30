package com.tavemakers.surf.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) return;

            InputStream is;

            // 환경변수로 경로가 주어진 경우 (서버/도커)
            String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");
            if (firebaseConfigPath != null) {
                is = new FileInputStream(firebaseConfigPath);
            }
            // 환경변수 없으면 classpath에서 로드 (로컬)
            else {
                is = getClass().getClassLoader()
                        .getResourceAsStream("firebase/tave-surf-dev-firebase-adminsdk.json");

                if (is == null) {
                    throw new IllegalStateException(
                            "Firebase service account json not found in classpath"
                    );
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}