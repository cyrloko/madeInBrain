package io.madeinbrain.service;

import io.madeinbrain.entity.Source;
import io.madeinbrain.entity.Theory;
import org.springframework.stereotype.Service;

@Service
public class ConfidenceScoreCalculatorService {

    public double calculateConfidenceScore(Theory theory) {
        if (theory.getSources() == null || theory.getSources().isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        double totalWeight = 0.0;

        for (Source source : theory.getSources()) {
            double sourceWeight = calculateSourceWeight(source);
            double sourceScore = source.getReliability();

            totalScore += sourceScore * sourceWeight;
            totalWeight += sourceWeight;
        }

        // Score de base basé sur les sources
        double baseScore = totalWeight > 0 ? totalScore / totalWeight : 0.0;

        // Ajustements selon différents critères
        double diversityBonus = calculateDiversityBonus(theory);
        double consensusBonus = calculateConsensusBonus(theory);
        double expertBonus = calculateExpertBonus(theory);

        double finalScore = baseScore + diversityBonus + consensusBonus + expertBonus;

        // Limiter le score entre 0 et 100
        return Math.max(0.0, Math.min(100.0, finalScore));
    }

    private double calculateSourceWeight(Source source) {
        double weight = 1.0;

        // Poids selon le type de source
        switch (source.getType()) {
            case JOURNAL:
                weight *= 1.5; // Articles de journaux scientifiques
                break;
            case REPORT:
                weight *= 1.3; // Rapports officiels
                break;
            case BOOK:
                weight *= 1.2; // Livres
                break;
            case DATA:
                weight *= 1.4; // Données brutes
                break;
            case CONFERENCE:
                weight *= 1.1; // Conférences
                break;
            case THESIS:
                weight *= 1.0; // Thèses
                break;
            case WEB:
                weight *= 0.8; // Sources web
                break;
        }

        // Poids selon la fiabilité de la source
        weight *= source.getReliability() / 100.0;

        // Poids selon l'âge de la source (plus récent = plus de poids)
        int currentYear = java.time.LocalDate.now().getYear();
        int sourceAge = currentYear - source.getYear();
        if (sourceAge <= 5) {
            weight *= 1.2; // Sources récentes
        } else if (sourceAge <= 10) {
            weight *= 1.0; // Sources moyennement récentes
        } else {
            weight *= 0.8; // Sources anciennes
        }

        return weight;
    }

    private double calculateDiversityBonus(Theory theory) {
        if (theory.getSources().size() < 2) return 0.0;

        long distinctTypes = theory.getSources().stream()
                .map(Source::getType)
                .distinct()
                .count();

        long distinctAuthors = theory.getSources().stream()
                .map(Source::getAuthor)
                .distinct()
                .count();

        // Bonus pour la diversité des types de sources et des auteurs
        double typeBonus = Math.min(3.0, distinctTypes * 0.5);
        double authorBonus = Math.min(5.0, distinctAuthors * 0.3);

        return typeBonus + authorBonus;
    }

    private double calculateConsensusBonus(Theory theory) {
        if (theory.getSources().size() < 3) return 0.0;

        // Bonus si la majorité des sources sont très fiables (>90%)
        long highReliabilitySources = theory.getSources().stream()
                .mapToLong(source -> source.getReliability() > 90 ? 1 : 0)
                .sum();

        double ratio = (double) highReliabilitySources / theory.getSources().size();

        if (ratio > 0.8) return 5.0; // 80% des sources très fiables
        if (ratio > 0.6) return 3.0; // 60% des sources très fiables
        if (ratio > 0.4) return 1.0; // 40% des sources très fiables

        return 0.0;
    }

    private double calculateExpertBonus(Theory theory) {
        // Bonus basé sur le nombre de validations d'experts
        if (theory.getExpertValidations() == null) return 0.0;

        return Math.min(10.0, theory.getExpertValidations() * 0.5);
    }
}