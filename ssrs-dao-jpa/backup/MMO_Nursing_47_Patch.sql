

/****** Object:  Table [dbo].[RANK]    Script Date: 12/11/2019 5:24:40 PM ******/
SET ANSI_NULLS ON
GO
CREATE TABLE [SEA_SERVICE_CAPACITY](
	[CAPACITY_ID] [numeric](3, 0) IDENTITY(1,1) NOT NULL,
	[ENG_DESC] [nvarchar](50) NOT NULL,
	[CHI_DESC] [nvarchar](40) NULL,
	[CREATE_BY] [nvarchar](50) NULL,
	[CREATE_DATE] [datetime] NULL,
	[LASTUPD_BY] [nvarchar](50) NULL,
	[LASTUPD_DATE] [datetime] NULL,
	[ROWVERSION] [int] NULL,
PRIMARY KEY CLUSTERED
(
	[CAPACITY_ID] ASC
)
) ON [PRIMARY]

GO





DELETE FROM SEA_SERVICE_CAPACITY;
GO
SET IDENTITY_INSERT SEA_SERVICE_CAPACITY ON

INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	1	,'MASTER',N'船長', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	2	,'CHIEF OFFICER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	3	,'SECOND OFFICER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	4	,'THIRD OFFICER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	5	,'NAVIGATING OFFICER',N'導航員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	6	,'NAVIGATING CADET',N'導航見習生', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	7	,'RADIO OFFICER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	8	,'CHIEF ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	9	,'SECOND ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	10	,'THIRD ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	11	,'FOURTH ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	12	,'JUNIOR ENGINEEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	13	,'ENGINEER OFFICER (WATCH KEEPER)',N'輪機師(值班員)', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	14	,'JR. ENGINEER OFFICER (DAYWORKER)',N'初級輪機師 (日間工)', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	15	,'ELECT. OFFICER',N'電氣師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	16	,'JR. ELECT. OFFICER',N'初級電氣師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	17	,'ENGINEER CADET',N'輪機見習生', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	18	,'BOSUN',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	19	,'CARPENTER',N'木匠', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	20	,'SEAMAN I',N'一級水手', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	21	,'SEAMAN II',N'二級水手', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	22	,'SEAMAN III',N'三級水手', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	23	,'JUNIOR SEAMAN',N'初級水手', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	24	,'FITTER (ENGINE)',N'輪機打磨匠', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	25	,'FITTER (ELECTRICAL)',N'電氣匠', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	26	,'FITTER (PUMPMAN)',N'泵工', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	27	,'ASST. FITTER (ENGINE)',N'助理輪機打磨匠', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	28	,'ASST. FITTER (ELECTRICAL)',N'助理電氣匠', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	29	,'ASST. FITTER (PUMPMAN)',N'助理泵工', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	30	,'MOTORMAN I',N'一級摩托', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	31	,'MOTORMAN II',N'二級摩托', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	32	,'MOTORMAN III',N'三級摩托', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	33	,'JUNIOR MOTORMAN',N'初級摩托', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	34	,'C.P.O.',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	35	,'P.O.',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	36	,'G.P. I',N'一級全能海員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	37	,'G.P. II',N'二級全能海員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	38	,'G.P. III',N'三級全能海員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	39	,'JUNIOR G.P.',N'初級全能海員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	40	,'G.P. (D) I',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	41	,'G.P. (D) II',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	43	,'G.P. (E) I',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	44	,'G.P. (E) II',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	45	,'G.P. (E) III',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	46	,'CHIEF STEWARD',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	47	,'SENIOR STEWARD',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	48	,'STEWARD',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	49	,'STEWARD GRADE 1',N'一級管事', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	50	,'STEWARD GRADE 2',N'二級管事', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	51	,'STEWARD GRADE 3',N'三級管事', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	52	,'JUNIOR STEWARD',N'初級管事', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	53	,'CHIEF COOK',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	54	,'COOK GRADE 1',N'一級廚師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	55	,'COOK GRADE 2',N'二級廚師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	56	,'COOK GRADE 3',N'三級廚師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	57	,'SECOND COOK',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	58	,'CREW COOK',N'船員廚師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	59	,'JUNIOR COOK',N'初級廚師', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	60	,'CABIN ATTENDANT',N'客艙服務員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	61	,'ASST. SUPERVISOR',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	62	,'REPAIRMAN',N'修理員', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	63	,'LAUNDRYMAN',N'洗衣工人', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	64	,'PURSER',N'事務長', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	65	,'OTHERS (SPECIFY)','其他', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	66	,'WRITER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	67	,'FIRST ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	68	,'C.P.O.(E)',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	69	,'JUNIOR NAVIGATING OFFICER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	70	,'CASSAB',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	71	,'RADIO CADET',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	72	,'DECK FITTER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	73	,'SENIOR NAVIGATING CADET',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	74	,'SENIOR ENGINEER CADET',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	75	,'POLITICAL COMMISSAR',N'政委', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	76	,'GAS ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	77	,'P.O.E.R.',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	78	,'ELECTRICAL CADET',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	79	,'NVO TRAINEE',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	80	,'NVO (1)',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	81	,'FIFTH ENGINEER',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	82	,'G.P. (D) III',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	83	,'SERVICE AMBASSADOR',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO SEA_SERVICE_CAPACITY (CAPACITY_ID, ENG_DESC, CHI_DESC, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES(	84	,'SERVICE ATTENDANT',NULL, 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);

SET IDENTITY_INSERT SEA_SERVICE_CAPACITY OFF
