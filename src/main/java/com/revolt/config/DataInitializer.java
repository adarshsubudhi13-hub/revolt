package com.revolt.config;

import com.revolt.model.*;
import com.revolt.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeInDeviceRepository tradeInRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository,
                           TradeInDeviceRepository tradeInRepository, OrderRepository orderRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.tradeInRepository = tradeInRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users if not present
        if (userRepository.count() == 0) {
            User admin = new User("Anita Desai (Admin)", "admin@revolt.com", "9999900001", passwordEncoder.encode("admin123"), Role.ADMIN);
            admin.setIsEmailVerified(true);
            admin.setIsPhoneVerified(true);
            userRepository.save(admin);

            User agent = new User("Suresh Kumar (Agent)", "agent@revolt.com", "9999900002", passwordEncoder.encode("agent123"), Role.AGENT);
            agent.setIsEmailVerified(true);
            agent.setIsPhoneVerified(true);
            userRepository.save(agent);

            User customer = new User("Rohit Sharma", "rohit@example.com", "9876543210", passwordEncoder.encode("user123"), Role.CUSTOMER);
            customer.setIsEmailVerified(true);
            customer.setIsPhoneVerified(true);
            customer.setAvailablePoints(25000L); // 25,000 points
            customer.setTrustScore(98);
            customer.setLoyaltyTier(LoyaltyTier.GOLD);
            userRepository.save(customer);

            System.out.println(">>> [REVOLT SEED] Users initialized: admin, agent, customer.");
        }

        // 2. Seed Vast Premium Product Catalog if empty or small
        if (productRepository.count() < 10) {
            productRepository.deleteAll();

            // SMARTPHONES
            addProduct("Apple iPhone 16 Pro Max (512 GB) - Desert Titanium", "Smartphones", "Apple",
                    "A18 Pro chip, 48MP Fusion camera with 5x telephoto, Grade 5 Titanium finish, Action button, and Camera Control.",
                    164900.0, 15, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.9-inch Super Retina XDR OLED 120Hz\",\"Processor\":\"A18 Pro\",\"Storage\":\"512 GB\",\"Color\":\"Desert Titanium\",\"Camera\":\"48MP + 48MP Ultra-Wide + 12MP 5x Telephoto\"}", 4.9, 88);

            addProduct("Apple iPhone 15 Pro (256 GB) - Natural Titanium", "Smartphones", "Apple",
                    "Aerospace-grade titanium design, A17 Pro chip, 48MP main camera system with 3x optical zoom and USB-C 3.0.",
                    124900.0, 20, "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.1-inch Super Retina XDR 120Hz\",\"Processor\":\"A17 Pro\",\"Storage\":\"256 GB\",\"Color\":\"Natural Titanium\"}", 4.8, 142);

            addProduct("Samsung Galaxy S24 Ultra (512 GB) - Titanium Gray", "Smartphones", "Samsung",
                    "Galaxy AI powerhouse with built-in S-Pen, Snapdragon 8 Gen 3 for Galaxy, and 200MP Quad Telephoto camera with 100x Space Zoom.",
                    129999.0, 18, "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.8-inch Dynamic AMOLED 2X 120Hz\",\"RAM\":\"12 GB\",\"Storage\":\"512 GB\",\"Processor\":\"Snapdragon 8 Gen 3\"}", 4.8, 96);

            addProduct("Samsung Galaxy Z Fold 5 (512 GB) - Phantom Black", "Smartphones", "Samsung",
                    "Next-gen foldable smartphone with 7.6-inch Dynamic AMOLED 2X interior display, Flex Mode, and multi-window productivity.",
                    154999.0, 8, "https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"7.6-inch Foldable AMOLED + 6.2-inch Cover\",\"RAM\":\"12 GB\",\"Storage\":\"512 GB\"}", 4.7, 45);

            addProduct("OnePlus 12 5G (16GB RAM + 512GB) - Silky Black", "Smartphones", "OnePlus",
                    "4th Gen Hasselblad Camera system, 5400 mAh high-density battery with 100W SUPERVOOC and 50W AIRVOOC wireless charging.",
                    64999.0, 25, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.82-inch 2K 120Hz ProXDR\",\"RAM\":\"16 GB LPDDR5X\",\"Storage\":\"512 GB UFS 4.0\",\"Battery\":\"5400 mAh 100W\"}", 4.8, 110);

            addProduct("Google Pixel 9 Pro (256 GB) - Obsidian", "Smartphones", "Google",
                    "Google Tensor G4 processor with integrated Gemini Nano AI, pro triple camera system with Super Res Zoom Video up to 20x.",
                    109999.0, 12, "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.3-inch Super Actua LTPO OLED 120Hz\",\"RAM\":\"16 GB\",\"Processor\":\"Google Tensor G4\",\"Storage\":\"256 GB\"}", 4.7, 52);

            addProduct("Xiaomi 14 Ultra (16GB + 512GB) - White Ceramic", "Smartphones", "Xiaomi",
                    "Co-engineered with Leica featuring 1-inch Sony LYT-900 sensor with stepless variable aperture and Quad 50MP Leica optics.",
                    99999.0, 10, "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.73-inch WQHD+ AMOLED 120Hz\",\"Camera\":\"Quad 50MP Leica Optics\",\"Processor\":\"Snapdragon 8 Gen 3\"}", 4.8, 38);

            addProduct("Samsung Galaxy S23 FE (128 GB) - Mint", "Smartphones", "Samsung",
                    "Premium Galaxy S-series flagship experience with dynamic camera system, pro-grade nighttime portraits, and smooth 120Hz AMOLED.",
                    39999.0, 30, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"6.4-inch Dynamic AMOLED 2X\",\"RAM\":\"8 GB\",\"Storage\":\"128 GB\",\"Battery\":\"4500 mAh\"}", 4.6, 75);

            // LAPTOPS & COMPUTERS
            addProduct("Apple MacBook Pro 16\" (M3 Max Chip, 36GB RAM, 1TB SSD) - Space Black", "Laptops", "Apple",
                    "Liquid Retina XDR display, up to 22 hours battery life, 16-core CPU, 40-core GPU, studio-quality mics, and six-speaker sound.",
                    349900.0, 6, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80",
                    "{\"Processor\":\"Apple M3 Max\",\"RAM\":\"36 GB Unified\",\"Storage\":\"1 TB SSD\",\"Display\":\"16.2-inch Liquid Retina XDR 120Hz\"}", 4.9, 64);

            addProduct("Apple MacBook Air 15\" (M2 Chip, 16GB, 512GB SSD) - Starlight", "Laptops", "Apple",
                    "Strikingly thin design with expansive 15.3-inch Liquid Retina display, silent fanless architecture, MagSafe 3 charging.",
                    134900.0, 14, "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=600&auto=format&fit=crop&q=80",
                    "{\"Processor\":\"Apple M2\",\"RAM\":\"16 GB Unified\",\"Storage\":\"512 GB SSD\",\"Weight\":\"1.51 kg\"}", 4.8, 92);

            addProduct("Dell XPS 15 OLED (Core i9 13th Gen, 32GB RAM, 1TB SSD, RTX 4070)", "Laptops", "Dell",
                    "Stunning 3.5K OLED InfinityEdge touch display, CNC machined aluminum chassis, carbon fiber palm rest, studio audio.",
                    229990.0, 8, "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"15.6-inch 3.5K OLED Touch\",\"Processor\":\"Intel Core i9-13900H\",\"Graphics\":\"NVIDIA RTX 4070 8GB\",\"RAM\":\"32 GB\"}", 4.8, 41);

            addProduct("Lenovo ThinkPad X1 Carbon Gen 12 (Intel Core Ultra 7, 32GB, 1TB SSD)", "Laptops", "Lenovo",
                    "The gold standard for enterprise mobility: featherlight carbon-fiber chassis, Intel AI Boost NPU, 2.8K OLED display.",
                    199990.0, 9, "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"14-inch 2.8K OLED 120Hz\",\"Processor\":\"Intel Core Ultra 7 155H\",\"Weight\":\"1.09 kg\",\"Security\":\"dTPM 2.0\"}", 4.7, 35);

            addProduct("ASUS ROG Zephyrus G16 (Intel Core Ultra 9, 32GB, 2TB SSD, RTX 4080)", "Laptops", "ASUS",
                    "Ultra-thin premium gaming and creative powerhouse with 2.5K 240Hz ROG Nebula OLED display and CNC Slash Lighting.",
                    259990.0, 7, "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"16-inch 2.5K 240Hz OLED\",\"Processor\":\"Intel Core Ultra 9\",\"Graphics\":\"NVIDIA RTX 4080 12GB\",\"RAM\":\"32 GB LPDDR5X\"}", 4.9, 29);

            addProduct("HP Spectre x360 2-in-1 (Intel Core Ultra 7, 16GB, 1TB SSD)", "Laptops", "HP",
                    "360-degree convertible laptop with 2.8K OLED IMAX Enhanced touchscreen, 9MP AI camera with night mode, premium stylus included.",
                    154990.0, 11, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"14-inch 2.8K OLED 120Hz Touch\",\"Processor\":\"Intel Core Ultra 7\",\"RAM\":\"16 GB\",\"FormFactor\":\"2-in-1 Convertible\"}", 4.7, 48);

            // TABLETS
            addProduct("Apple iPad Pro 13\" (M4 Chip, 256GB, Wi-Fi) - Space Black", "Tablets", "Apple",
                    "Unbelievably thin design (5.1mm), groundbreaking Ultra Retina XDR Tandem OLED display, blistering M4 neural engine performance.",
                    129900.0, 15, "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"13-inch Ultra Retina Tandem OLED\",\"Processor\":\"Apple M4\",\"Storage\":\"256 GB\",\"Accessories\":\"Supports Apple Pencil Pro\"}", 4.9, 58);

            addProduct("Apple iPad Air 11\" (M2 Chip, 128GB, Wi-Fi) - Blue", "Tablets", "Apple",
                    "Supercharged by the M2 chip, 11-inch Liquid Retina display, landscape front camera with Center Stage, all-day battery.",
                    59900.0, 22, "https://images.unsplash.com/photo-1561154464-82e9adf32764?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"11-inch Liquid Retina\",\"Processor\":\"Apple M2\",\"Storage\":\"128 GB\",\"Weight\":\"462g\"}", 4.8, 86);

            addProduct("Samsung Galaxy Tab S9 Ultra (512GB, Wi-Fi + 5G) - Graphite", "Tablets", "Samsung",
                    "Enormous 14.6-inch Dynamic AMOLED 2X display, IP68 water & dust resistance, bundled S-Pen with near-zero latency, DeX desktop mode.",
                    122999.0, 10, "https://images.unsplash.com/photo-1585792180666-f7347c490ee2?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"14.6-inch Dynamic AMOLED 2X 120Hz\",\"RAM\":\"12 GB\",\"Storage\":\"512 GB\",\"Battery\":\"11,200 mAh\"}", 4.8, 44);

            addProduct("OnePlus Pad 2 (12GB RAM + 256GB) - Nimbus Gray", "Tablets", "OnePlus",
                    "Flagship Snapdragon 8 Gen 3 tablet with 3K 144Hz 7:5 ReadFit display, 6 stereo speakers with Omnibearing Sound, 67W fast charging.",
                    39999.0, 18, "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"12.1-inch 3K 144Hz\",\"Processor\":\"Snapdragon 8 Gen 3\",\"RAM\":\"12 GB\",\"Battery\":\"9510 mAh 67W\"}", 4.7, 33);

            // AUDIO & HEADPHONES
            addProduct("Sony WH-1000XM5 Wireless Noise Cancelling Headphones", "Audio", "Sony",
                    "Industry-leading noise cancellation with two processors and 8 microphones. Auto NC Optimizer, 30-hour battery life, Multipoint.",
                    29990.0, 35, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                    "{\"Battery\":\"30 Hours\",\"ANC\":\"Dual Processor 8-Microphone\",\"FastCharge\":\"3 min charge for 3h playback\",\"Weight\":\"250g\"}", 4.9, 210);

            addProduct("Bose QuietComfort Ultra Wireless Noise Cancelling Headphones", "Audio", "Bose",
                    "World-class quiet, breakthrough spatialized audio with Bose Immersive Audio, custom-tuned sound with CustomTune technology.",
                    35900.0, 20, "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600&auto=format&fit=crop&q=80",
                    "{\"Battery\":\"24 Hours (18h Immersive Audio)\",\"Audio\":\"Bose Immersive Spatial Audio\",\"Bluetooth\":\"5.3 Snapdragon Sound\"}", 4.8, 95);

            addProduct("Apple AirPods Max (USB-C) - Midnight", "Audio", "Apple",
                    "Custom acoustic design with Apple-designed H1 chips in each cup, Computational Audio, Active Noise Cancellation with Transparency mode.",
                    59900.0, 16, "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=600&auto=format&fit=crop&q=80",
                    "{\"Chassis\":\"Anodized Aluminum + Stainless Steel Mesh\",\"Audio\":\"Personalized Spatial Audio\",\"Port\":\"USB-C Charging\"}", 4.7, 130);

            addProduct("Sennheiser Momentum 4 Wireless Audiophile Headphones", "Audio", "Sennheiser",
                    "Legendary Sennheiser signature sound, unmatched 60-hour battery life, audiophile-grade 42mm transducer system, adaptive ANC.",
                    29990.0, 24, "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600&auto=format&fit=crop&q=80",
                    "{\"Battery\":\"60 Hours\",\"Drivers\":\"42mm Transducers\",\"Codecs\":\"aptX Adaptive, AAC, SBC\"}", 4.8, 72);

            addProduct("Marshall Stanmore III Home Bluetooth Speaker", "Audio", "Marshall",
                    "Iconic vintage Marshall stage-look speaker with an expansive stereo soundstage, Dynamic Loudness, 80-watt Class D amplification.",
                    39999.0, 12, "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600&auto=format&fit=crop&q=80",
                    "{\"Power\":\"80 Watts Class D\",\"Connectivity\":\"Bluetooth 5.2, 3.5mm AUX, RCA\",\"Frequency\":\"45–20,000 Hz\"}", 4.8, 64);

            // WEARABLES & SMARTWATCHES
            addProduct("Apple Watch Ultra 2 (GPS + Cellular, 49mm Titanium Case)", "Wearables", "Apple",
                    "The ultimate sports & adventure watch: rugged aerospace titanium case, 3000 nits brightest display, precision dual-frequency GPS.",
                    89900.0, 14, "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600&auto=format&fit=crop&q=80",
                    "{\"Case\":\"49mm Aerospace Titanium\",\"Display\":\"3000 nits Always-On\",\"WaterResistance\":\"100m Dive Certified\",\"Battery\":\"Up to 72 hours\"}", 4.9, 115);

            addProduct("Apple Watch Series 9 (GPS, 45mm Midnight Aluminum)", "Wearables", "Apple",
                    "Powerful S9 SiP chip, Double Tap gesture control, on-device Siri processing, 2000 nits display, comprehensive health monitoring.",
                    44900.0, 25, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"Always-On Retina 2000 nits\",\"Sensors\":\"ECG, Blood Oxygen, Temperature\",\"Gesture\":\"Double Tap\"}", 4.8, 140);

            addProduct("Samsung Galaxy Watch 6 Classic (47mm Bluetooth + LTE)", "Wearables", "Samsung",
                    "Physical rotating bezel, sapphire crystal glass display, advanced body composition analysis (BIA), ECG & blood pressure monitoring.",
                    36999.0, 20, "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600&auto=format&fit=crop&q=80",
                    "{\"Bezel\":\"Rotating Physical Stainless Steel\",\"Sensors\":\"BioActive (ECG, BIA, HR)\",\"Display\":\"1.5-inch Super AMOLED\"}", 4.7, 85);

            addProduct("Garmin Fenix 7 Pro Solar (47mm Multisport GPS Watch)", "Wearables", "Garmin",
                    "Solar charging lens for weeks of battery life, built-in LED flashlight, endurance score, multi-band GPS with SatIQ technology.",
                    89990.0, 10, "https://images.unsplash.com/photo-1510017803434-a899398421b3?w=600&auto=format&fit=crop&q=80",
                    "{\"Battery\":\"Up to 22 days with Solar\",\"Lens\":\"Power Glass Solar\",\"Maps\":\"TopoActive Multi-Continent\"}", 4.9, 46);

            // GAMING CONSOLES & HANDHELDS
            addProduct("Sony PlayStation 5 Slim Console (1TB SSD, Disc Edition)", "Gaming", "Sony",
                    "High-speed 1TB custom SSD, ray tracing support, 4K gaming up to 120fps, DualSense haptic feedback and Tempest 3D AudioTech.",
                    54990.0, 16, "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600&auto=format&fit=crop&q=80",
                    "{\"Storage\":\"1 TB Custom NVMe SSD\",\"Resolution\":\"4K 120Hz / 8K output\",\"Controller\":\"DualSense Wireless Included\"}", 4.9, 185);

            addProduct("Microsoft Xbox Series X Console (1TB SSD)", "Gaming", "Microsoft",
                    "12 teraflops of raw graphic processing power, DirectX Raytracing, Quick Resume for multiple games, Xbox Game Pass compatible.",
                    54990.0, 12, "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=600&auto=format&fit=crop&q=80",
                    "{\"GPU\":\"12 TFLOPS AMD RDNA 2\",\"Storage\":\"1 TB Custom NVMe SSD\",\"Audio\":\"Dolby Vision & Dolby Atmos\"}", 4.8, 94);

            addProduct("Nintendo Switch OLED Model (White Joy-Con)", "Gaming", "Nintendo",
                    "Vibrant 7-inch OLED screen, wide adjustable tabletop stand, dock with wired LAN port, 64 GB internal storage, enhanced audio.",
                    32990.0, 25, "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"7-inch OLED Touchscreen\",\"Modes\":\"TV, Tabletop, Handheld\",\"Battery\":\"4.5–9 hours\"}", 4.8, 120);

            addProduct("Valve Steam Deck OLED (512GB Handheld Gaming PC)", "Gaming", "Valve",
                    "7.4-inch 90Hz HDR OLED display, custom AMD APU, 50Wh battery offering 3–12 hours of gameplay, full Steam library compatibility.",
                    58000.0, 8, "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
                    "{\"Display\":\"7.4-inch 90Hz HDR OLED\",\"APU\":\"6nm AMD Zen 2 + RDNA 2\",\"Storage\":\"512 GB High Speed NVMe\"}", 4.9, 68);

            // CAMERAS & DRONES
            addProduct("Sony Alpha A7 IV Full-Frame Mirrorless Camera (Body)", "Cameras", "Sony",
                    "33MP Exmor R full-frame sensor, BIONZ XR processing engine, 4K 60p 10-bit 4:2:2 video recording, 759-point phase-detection AF.",
                    215000.0, 6, "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&auto=format&fit=crop&q=80",
                    "{\"Sensor\":\"33MP Full-Frame Exmor R CMOS\",\"Video\":\"4K 60p 10-bit 4:2:2\",\"Stabilization\":\"5-axis in-body 5.5-step\"}", 4.9, 54);

            addProduct("DJI Mini 4 Pro Drone (DJI RC 2 Smart Controller Bundle)", "Cameras", "DJI",
                    "Sub-249g ultra-lightweight drone, omnidirectional obstacle sensing, 4K/60fps HDR True Vertical Shooting, 20km FHD video transmission.",
                    94900.0, 9, "https://images.unsplash.com/photo-1527977966376-1c8408f9f108?w=600&auto=format&fit=crop&q=80",
                    "{\"Weight\":\"< 249g\",\"Camera\":\"4K/60fps HDR 48MP\",\"Transmission\":\"DJI O4 20km\",\"Sensing\":\"Omnidirectional Obstacle\"}", 4.9, 41);

            System.out.println(">>> [REVOLT SEED] Vast catalog loaded: " + productRepository.count() + " premium electronics products.");
        }

        // 3. Demo Trade-In and Demo Order initialization
        if (orderRepository.count() == 0) {
            User customer = userRepository.findByEmail("rohit@example.com").orElse(null);
            User agent = userRepository.findByEmail("agent@revolt.com").orElse(null);
            Product sampleProduct = productRepository.findAll().get(0);

            if (customer != null && agent != null && sampleProduct != null) {
                TradeInDevice sampleTradeIn = new TradeInDevice();
                sampleTradeIn.setUserId(customer.getId());
                sampleTradeIn.setCategory("Smartphones");
                sampleTradeIn.setBrand("Apple");
                sampleTradeIn.setModel("iPhone 13");
                sampleTradeIn.setSpecsJson("{\"Storage\":\"128GB\",\"Color\":\"Midnight Blue\"}");
                sampleTradeIn.setAgeMonths(18);
                sampleTradeIn.setConditionGrade(ConditionGrade.GRADE_B);
                sampleTradeIn.setImei("358941094827103");
                sampleTradeIn.setHasOriginalBox(true);
                sampleTradeIn.setHasOriginalCharger(true);
                sampleTradeIn.setHasValidBill(true);
                sampleTradeIn.setEstimatedPoints(30000L);
                sampleTradeIn.setLockedUntil(LocalDateTime.now().plusDays(7));
                sampleTradeIn.setStatus(TradeInStatus.APPLIED_TO_ORDER);
                TradeInDevice savedTradeIn = tradeInRepository.save(sampleTradeIn);

                Order demoOrder = new Order();
                demoOrder.setOrderNumber("RV-20261108-7741");
                demoOrder.setUserId(customer.getId());
                demoOrder.setAgentId(agent.getId());
                demoOrder.setProductId(sampleProduct.getId());
                demoOrder.setProductTitle(sampleProduct.getTitle());
                demoOrder.setProductPrice(sampleProduct.getPrice());
                demoOrder.setQuantity(1);
                demoOrder.setTradeInDeviceId(savedTradeIn.getId());
                demoOrder.setTotalAmount(sampleProduct.getPrice());
                demoOrder.setPointsRedeemed(30000L);
                demoOrder.setCashPaid(sampleProduct.getPrice() - 30000.0);
                demoOrder.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
                demoOrder.setPaymentStatus(PaymentStatus.PAID);
                demoOrder.setShippingAddressJson("{\"name\":\"Rohit Sharma\",\"phone\":\"9876543210\",\"address\":\"Flat 402, Green Glen Layout, Bellandur\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"pincode\":\"560103\"}");
                demoOrder.setHandoverOtp("4829");
                orderRepository.save(demoOrder);

                System.out.println(">>> [REVOLT SEED] Demo Order #RV-20261108-7741 seeded.");
            }
        }
    }

    private void addProduct(String title, String category, String brand, String desc,
                            Double price, Integer stock, String imgUrl, String specsJson, Double rating, Integer reviews) {
        Product p = new Product();
        p.setTitle(title);
        p.setCategory(category);
        p.setBrand(brand);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setImageUrl(imgUrl);
        p.setSpecsJson(specsJson);
        p.setRating(rating);
        p.setReviewCount(reviews);
        p.setIsActive(true);
        productRepository.save(p);
    }
}
